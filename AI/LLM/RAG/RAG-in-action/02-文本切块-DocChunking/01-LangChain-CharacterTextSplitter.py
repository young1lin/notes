from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import CharacterTextSplitter
loader = TextLoader("90-文档-Data/山西文旅/云冈石窟.txt")
documents = loader.load()
# 设置分块器，指定块的大小为50个字符，无重叠
text_splitter = CharacterTextSplitter(
    chunk_size=100,  # 每个文本块的大小为50个字符
    chunk_overlap=10,  # 文本块之间没有重叠部分
)
chunks = text_splitter.split_documents(documents)
print("\n=== 文档分块结果 ===")
for i, chunk in enumerate(chunks, 1):
    print(f"\n--- 第 {i} 个文档块 ---")
    print(f"内容: {chunk.page_content}")
    print(f"元数据: {chunk.metadata}")
    print("-" * 50)
