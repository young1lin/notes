# 1. 加载文档
import os
from dotenv import load_dotenv
# 加载环境变量
load_dotenv()

from langchain_community.document_loaders import WebBaseLoader

loader = WebBaseLoader(
    web_paths=("https://zh.wikipedia.org/wiki/黑神话：悟空",)
)
docs = loader.load()

# 2. 文档分块
from langchain_text_splitters import RecursiveCharacterTextSplitter

text_splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=200)
all_splits = text_splitter.split_documents(docs)

# 3. 设置嵌入模型
from langchain_huggingface import HuggingFaceEmbeddings

embeddings = HuggingFaceEmbeddings(
    model_name="BAAI/bge-small-zh-v1.5",
    model_kwargs={'device': 'cpu'},
    encode_kwargs={'normalize_embeddings': True}
)

# 4. 创建向量存储
from langchain_core.vectorstores import InMemoryVectorStore

vector_store = InMemoryVectorStore(embeddings)
vector_store.add_documents(all_splits)

# 5. 构建用户查询
question = "黑悟空有哪些游戏场景？"

# 6. 在向量存储中搜索相关文档，并准备上下文内容
retrieved_docs = vector_store.similarity_search(question, k=3)
docs_content = "\n\n".join(doc.page_content for doc in retrieved_docs)

# 7. 构建提示模板
from langchain_core.prompts import ChatPromptTemplate

prompt = ChatPromptTemplate.from_template("""
                基于以下上下文，回答问题。如果上下文中没有相关信息，
                请说"我无法从提供的上下文中找到相关信息"。
                上下文: {context}
                问题: {question}
                回答:"""
                                          )

# 8. 使用大语言模型生成答案
from langchain_openai import ChatOpenAI

llm = ChatOpenAI(
    model="deepseek-reasoner",  # DeepSeek API 支持的模型名称
    base_url="https://api.deepseek.com/v1",
    temperature=0.7,        # 控制输出的随机性(0-1之间,越大越随机)
    max_tokens=2048,        # 最大输出长度
    top_p=0.95,            # 控制输出的多样性(0-1之间)
    presence_penalty=0.0,   # 重复惩罚系数(-2.0到2.0之间)
    frequency_penalty=0.0,  # 频率惩罚系数(-2.0到2.0之间)
    api_key=os.getenv("DEEPSEEK_API_KEY")  # 从环境变量加载API key
)
answer = llm.invoke(prompt.format(question=question, context=docs_content))
print(answer)


