# 导入 LlamaIndex 相关模块
from llama_index.core import VectorStoreIndex, Settings
from llama_index.readers.file import PyMuPDFReader
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding

# 全局设置
Settings.llm = OpenAI(model="gpt-3.5-turbo")
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# ---------------------------
# 1. 解析 PDF 结构，提取文本和表格
# ---------------------------
file_path = "90-文档-Data/复杂PDF/billionaires_page-1-5.pdf"  # 修改为你的文件路径

# 使用 PyMuPDFReader 加载 PDF
reader = PyMuPDFReader()
documents = reader.load(file_path)

# ---------------------------
# 2. 创建向量索引
# ---------------------------
index = VectorStoreIndex.from_documents(documents)

# ---------------------------
# 3. 创建查询引擎
# ---------------------------
query_engine = index.as_query_engine(
    similarity_top_k=3,
    verbose=True
)

# ---------------------------
# 4. 测试查询
# ---------------------------
query = "Who was the second richest billionaire in 2023?"
response = query_engine.query(query)
print("Query:", query)
print("Response:", response)

# 显示检索到的文本块
print("\nRetrieved Text Chunks:")
for i, source_node in enumerate(response.source_nodes):
    print(f"\nChunk {i+1}:")
    print("Text content:")
    print(source_node.text)
    print("-" * 50)

# 生成回答
response = query_engine.query(query)
print("Query:", query)
print("Response:", response)




