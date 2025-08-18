import os
from typing import List
from unstructured.partition.pdf import partition_pdf
import pandas as pd

# 导入 LlamaIndex 相关模块
from llama_index.core import VectorStoreIndex, Settings
from llama_index.readers.file import PyMuPDFReader
from llama_index.experimental.query_engine import PandasQueryEngine
from llama_index.core.schema import IndexNode
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core.retrievers import RecursiveRetriever
from llama_index.core.query_engine import RetrieverQueryEngine
from llama_index.core import get_response_synthesizer

# 全局设置
Settings.llm = OpenAI(model="gpt-3.5-turbo")
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# ---------------------------
# 1. 解析 PDF 结构，提取文本和表格
# ---------------------------
file_path = "90-文档-Data/复杂PDF/billionaires_page-1-5.pdf"  # 修改为你的文件路径

elements = partition_pdf(
    file_path,
    strategy="hi_res",  # 使用高精度策略
    extract_tables_in_paragraphs=True,  # 提取段落中的表格
    include_metadata=True  # 包含元数据信息
)  # 解析PDF文档

# 创建一个元素ID到元素的映射
element_map = {element.id: element for element in elements if hasattr(element, 'id')}

for element in elements:
    if element.category == "Table":
        print("\n表格数据:")
        print("表格元数据:", vars(element.metadata))  # 使用vars()显示所有元数据属性
        print("表格内容:")
        print(element.text)  # 打印表格文本内容
        
        # 获取并打印父节点信息
        parent_id = getattr(element.metadata, 'parent_id', None)
        if parent_id and parent_id in element_map:
            parent_element = element_map[parent_id]
            print("\n父节点信息:")
            print(f"类型: {parent_element.category}")
            print(f"内容: {parent_element.text}")
            if hasattr(parent_element, 'metadata'):
                print(f"父节点元数据: {vars(parent_element.metadata)}")  # 同样使用vars()显示所有元数据
        else:
            print(f"未找到父节点 (ID: {parent_id})")
        print("-" * 50)

text_elements = [el for el in elements if el.category == "Text"]
table_elements = [el for el in elements if el.category == "Table"]

# ---------------------------
# 2. 识别每个表格的年份
# ---------------------------
def extract_year_from_text(text):
    """从文本中提取年份（简单匹配 1900-2099 年的模式）"""
    import re
    match = re.search(r"\b(19\d{2}|20\d{2})\b", text)
    return match.group(0) if match else None

table_data = []
last_seen_year = None

for element in elements:
    if element.category == "Text":
        extracted_year = extract_year_from_text(element.text)
        if extracted_year:
            last_seen_year = extracted_year  # 记录最近的年份
    elif element.category == "Table":
        # 解析表格文本内容
        rows = element.text.strip().split('\n')
        header = rows[0].split()  # 假设第一行是表头
        data = [row.split() for row in rows[1:]]
        df = pd.DataFrame(data, columns=header)
        table_data.append({"table": df, "year": last_seen_year})  # 关联表格和年份

# ---------------------------
# 3. 创建 Pandas Query Engine 并查询
# ---------------------------
llm_for_table = OpenAI(model="gpt-4")

df_query_engines = [
    PandasQueryEngine(table_info["table"], llm=llm_for_table)
    for table_info in table_data
]

# 测试查询：查询 2023 年第二富豪的净资产
for idx, engine in enumerate(df_query_engines):
    year = table_data[idx]["year"]
    if year == "2023":  # 仅查询 2023 年的表格
        response = engine.query("Who is the second richest billionaire?")
        print(f"Year: {year}, Response: {response}")

# ---------------------------
# 4. 构建向量索引并进行检索
# ---------------------------
table_summaries = []
for idx, table_info in enumerate(table_data):
    table_text = table_info["table"].to_csv(index=False)
    year = table_info["year"]
    prompt = f"请用一句话总结{year}年表格的主要内容：\n\n{table_text}\n\n摘要："
    summary = llm_for_table.complete(prompt).text.strip()
    table_summaries.append(summary)
    print(f"自动生成的 {year} 年表格摘要：", summary)

df_nodes = [
    IndexNode(text=table_summaries[idx], index_id=f"table_{idx}")
    for idx in range(len(table_summaries))
]

vector_index = VectorStoreIndex(df_nodes)
vector_retriever = vector_index.as_retriever(similarity_top_k=1)

query_engine = RetrieverQueryEngine.from_args(
    vector_retriever, response_synthesizer=get_response_synthesizer(response_mode="compact")
)

# ---------------------------
# 5. 测试带有年份的查询
# ---------------------------
query = "Who was the second richest billionaire in 2023?"
response = query_engine.query(query)
print("Query:", query)
print("Response:", response)
