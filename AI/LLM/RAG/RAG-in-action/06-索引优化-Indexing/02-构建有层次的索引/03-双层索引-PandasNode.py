# 双层检索-富豪榜 - 需要pip install openpyxl
import os
from dotenv import load_dotenv
import pandas as pd
import logging
from llama_index.core import Document, VectorStoreIndex
from llama_index.core.node_parser import SentenceSplitter
from llama_index.core.schema import IndexNode
from llama_index.experimental.query_engine import PandasQueryEngine
from llama_index.core.retrievers import RecursiveRetriever
from llama_index.core.query_engine import RetrieverQueryEngine
from llama_index.core import get_response_synthesizer
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core import Settings

# 设置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# 加载环境变量
load_dotenv()

# 设置全局设置
Settings.llm = OpenAI(model="gpt-3.5-turbo")
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# 3. 加载Excel文件并准备数据
excel_file = "90-文档-Data/复杂PDF/十大富豪/世界十大富豪.xlsx"

# 初始化Node Parser
node_parser = SentenceSplitter(
    chunk_size=1024,  # 每个chunk的大小
    chunk_overlap=20,  # chunk之间的重叠大小
    include_metadata=True  # 包含元数据
)

# 存储所有表格的DataFrame和查询引擎
table_dfs = []
df_query_engines = []
documents = []

# 读取Excel文件中的所有sheet并插入数据
with pd.ExcelFile(excel_file) as xls:
    for sheet_name in xls.sheet_names:
        try:
            df = pd.read_excel(xls, sheet_name=sheet_name)
            logging.info(f"正在处理sheet: {sheet_name}")
            
            # 将DataFrame转换为字符串
            table_content = df.to_string(index=False)
            
            # 创建Document对象
            doc = Document(
                text=table_content,
                metadata={"table_name": sheet_name}
            )
            documents.append(doc)
            
            # 存储DataFrame和创建查询引擎
            table_dfs.append(df)
            df_query_engine = PandasQueryEngine(df, llm=Settings.llm)
            df_query_engines.append(df_query_engine)
            
            logging.info(f"成功处理sheet: {sheet_name}")
            
        except Exception as e:
            logging.error(f"处理sheet {sheet_name} 时出错: {str(e)}")
            logging.error(f"错误详情: {e.__class__.__name__}")
            continue

# 创建IndexNode对象
summaries = [
    f"This node provides information about the world's richest billionaires in {sheet_name}"
    for sheet_name in xls.sheet_names
]

df_nodes = [
    IndexNode(text=summary, index_id=f"pandas{idx}") # 每个表的细节
    for idx, summary in enumerate(summaries)
]

# 创建查询引擎映射
df_id_query_engine_mapping = {
    f"pandas{idx}": df_query_engine
    for idx, df_query_engine in enumerate(df_query_engines)
}

# 创建向量索引
vector_index = VectorStoreIndex(documents + df_nodes)
vector_retriever = vector_index.as_retriever(similarity_top_k=1)

# 创建递归检索器
recursive_retriever = RecursiveRetriever(
    "vector",
    retriever_dict={"vector": vector_retriever},
    query_engine_dict=df_id_query_engine_mapping,
    verbose=True,
)

# 创建响应合成器
response_synthesizer = get_response_synthesizer(response_mode="compact")

# 创建查询引擎
query_engine = RetrieverQueryEngine.from_args(
    recursive_retriever, response_synthesizer=response_synthesizer
)

def generate_answer(question):
    # 使用查询引擎生成答案
    response = query_engine.query(question)
    return str(response)

# 测试示例
if __name__ == "__main__":
    test_question = "2020年世界首富是谁？他的财富是多少？"
    answer = generate_answer(test_question)
    print(f"问题：{test_question}")
    print(f"答案：{answer}") 