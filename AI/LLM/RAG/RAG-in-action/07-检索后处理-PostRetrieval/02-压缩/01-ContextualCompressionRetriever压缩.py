# 导入所需的库
from langchain_cohere import CohereRerank
from langchain.retrievers.contextual_compression import ContextualCompressionRetriever
from langchain_core.documents import Document
from langchain_community.retrievers import BM25Retriever
from dotenv import load_dotenv
load_dotenv()

# 获取Cohere API key
# 地址：https://dashboard.cohere.com/api-keys
# 如果env文件没有设置CO_API_KEY，也可以通过以下方式
# import os
# api_key = 'XXXX'
# os.environ['CO_API_KEY'] = api_key

documents = [
    Document(
        page_content="五台山是中国四大佛教名山之一，以文殊菩萨道场闻名。",
        metadata={"source": "山西旅游指南"}
    ),
    Document(
        page_content="云冈石窟是中国三大石窟之一，以精美的佛教雕塑著称。",
        metadata={"source": "山西旅游指南"}
    ),
    Document(
        page_content="平遥古城是中国保存最完整的古代县城之一，被列为世界文化遗产。",
        metadata={"source": "山西旅游指南"}
    )
]
# 创建BM25检索器
retriever = BM25Retriever.from_documents(documents)
retriever.k = 3  # 设置返回前3个结果
# 设置Cohere重排序器
# 模型地址: https://huggingface.co/Cohere/rerank-multilingual-v3.0
compressor = CohereRerank(model="rerank-multilingual-v3.0")
# 创建ContextualCompressionRetriever
compression_retriever = ContextualCompressionRetriever(
    base_compressor=compressor,
    base_retriever=retriever
)
# 执行查询、重排和压缩
query = "山西有哪些著名的旅游景点？"
compressed_docs = compression_retriever.invoke(query)
# 输出压缩结果
print(f"查询：{query}\n")
print("重排并压缩后的结果：")
for i, doc in enumerate(compressed_docs, 1):
    print(f"{i}. {doc.page_content}")

