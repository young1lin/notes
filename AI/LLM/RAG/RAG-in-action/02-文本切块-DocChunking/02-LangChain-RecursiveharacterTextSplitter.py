from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
loader = TextLoader("90-文档-Data/山西文旅/云冈石窟.txt")
documents = loader.load()
# 定义分割符列表，按优先级依次使用
separators = ["\n\n", ".", "，", " "] # . 是句号，， 是逗号， 是空格
# 创建递归分块器，并传入分割符列表
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=100,
    chunk_overlap=10,
    separators=separators
)
chunks = text_splitter.split_documents(documents)
print("\n=== 文档分块结果 ===")
for i, chunk in enumerate(chunks, 1):
    print(f"\n--- 第 {i} 个文档块 ---")
    print(f"内容: {chunk.page_content}")
    print(f"元数据: {chunk.metadata}")
    print("-" * 50)