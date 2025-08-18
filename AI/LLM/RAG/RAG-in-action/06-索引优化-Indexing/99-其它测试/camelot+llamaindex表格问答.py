import os
from typing import List
import camelot

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

# 全局设置：使用 GPT-3.5-turbo 作为 LLM，选用小型号的 OpenAIEmbedding
Settings.llm = OpenAI(model="gpt-3.5-turbo")
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# ---------------------------
# 1. 加载 PDF 正文（叙述性文本）
# ---------------------------
file_path = "90-文档-Data/复杂PDF/billionaires_page.pdf"
reader = PyMuPDFReader()
docs = reader.load(file_path)

# ---------------------------
# 2. 利用 camelot 提取 PDF 中的表格pip
# ---------------------------
def get_tables(path: str, pages: List[int]):
    table_dfs = []
    for page in pages:
        table_list = camelot.read_pdf(path, pages=str(page))
        if len(table_list) > 0:
            table_df = table_list[0].df
            # 将第一行作为表头，并重置索引
            table_df = (
                table_df.rename(columns=table_df.iloc[0])
                .drop(table_df.index[0])
                .reset_index(drop=True)
            )
            table_dfs.append(table_df)
    return table_dfs

# 假设第3页为 2023 年富豪榜表格，第25页为年度统计表
table_dfs = get_tables(file_path, pages=[3, 25])

# ---------------------------
# 3. 针对每个表格创建 Pandas Query Engine
# ---------------------------
# 建议针对表格查询使用更强的 LLM，这里使用 GPT-4（也可使用其它模型）
llm_for_table = OpenAI(model="gpt-4")
df_query_engines = [
    PandasQueryEngine(table_df, llm=llm_for_table) for table_df in table_dfs
]

# 测试表格查询
response_table0 = df_query_engines[0].query(
    "What's the net worth of the second richest billionaire in 2023?"
)
print("Table0 response:", str(response_table0))
response_table1 = df_query_engines[1].query(
    "How many billionaires were there in 2009?"
)
print("Table1 response:", str(response_table1))

# ---------------------------
# 4. 自动生成每个表格的摘要（Summary）
# ---------------------------
# 对于每个表格，将其转换为 CSV 文本，再调用 LLM 生成摘要
table_summaries = []
for idx, table_df in enumerate(table_dfs):
    # 转换表格为文本格式（可以根据需要调整格式）
    table_text = table_df.to_csv(index=False)
    prompt = (
        "请用一句话总结下面表格的主要内容，描述表格展示的信息：\n\n"
        f"{table_text}\n\n摘要："
    )
    summary = llm_for_table.complete(prompt).text.strip()
    table_summaries.append(summary)
    print(f"自动生成的表格 {idx} 摘要：", summary)

# ---------------------------
# 5. 构建向量索引（整合正文节点和表格摘要节点）
# ---------------------------
# 5.1 从 PDF 正文构建节点（使用默认的文本拆分器）
doc_nodes = Settings.node_parser.get_nodes_from_documents(docs)

# 5.2 利用自动生成的摘要，为每个表格创建一个 IndexNode，并赋予唯一 index_id
df_nodes = [
    IndexNode(text=table_summaries[idx], index_id=f"pandas{idx}")
    for idx in range(len(table_summaries))
]

# 建立 IndexNode id 与 PandasQueryEngine 之间的映射
df_id_query_engine_mapping = {
    f"pandas{idx}": df_query_engine
    for idx, df_query_engine in enumerate(df_query_engines)
}

# 合并正文节点和表格节点构成最终索引
all_nodes = doc_nodes + df_nodes
vector_index = VectorStoreIndex(all_nodes)
vector_retriever = vector_index.as_retriever(similarity_top_k=1)

# ---------------------------
# 6. 利用 RecursiveRetriever 构建层级查询引擎
# ---------------------------
# 当检索到表格 IndexNode 时，进一步调用对应的 PandasQueryEngine
recursive_retriever = RecursiveRetriever(
    "vector",
    retriever_dict={"vector": vector_retriever},
    query_engine_dict=df_id_query_engine_mapping,
    verbose=True,
)

# 使用 compact 模式构建 Response Synthesizer
response_synthesizer = get_response_synthesizer(response_mode="compact")

# 构造最终的 RetrieverQueryEngine
query_engine = RetrieverQueryEngine.from_args(
    recursive_retriever, response_synthesizer=response_synthesizer
)

# ---------------------------
# 7. 执行查询，测试递归检索效果
# ---------------------------
# 示例 1：查询 2023 年第二富豪的净资产
query_1 = "What's the net worth of the second richest billionaire in 2023?"
response = query_engine.query(query_1)
print("Query:", query_1)
print("Response:", str(response))

# 示例 2：查询 2009 年有多少亿万富豪
query_2 = "How many billionaires were there in 2009?"
response = query_engine.query(query_2)
print("Query:", query_2)
print("Response:", str(response))

# 示例 3：查询排除规则（比如哪些富豪不在列表中）
query_3 = "Which billionaires are excluded from this list?"
response = query_engine.query(query_3)
print("Query:", query_3)
print("Response:", str(response))
