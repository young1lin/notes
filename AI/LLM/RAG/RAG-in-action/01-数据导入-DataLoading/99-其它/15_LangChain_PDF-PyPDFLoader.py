from langchain_openai import OpenAIEmbeddings, ChatOpenAI
from langchain_community.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import FAISS
from langchain.chains import RetrievalQA
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()

# 初始化 OpenAI 模型
embeddings = OpenAIEmbeddings(model="text-embedding-3-small")
llm = ChatOpenAI(model="gpt-3.5-turbo-0125", temperature=0)

# 加载 PDF 文件
loader = PyPDFLoader("data/PDF/uber_10q_march_2022.pdf")
documents = loader.load()

# 文本分割
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=1000,
    chunk_overlap=200,
    length_function=len,
    add_start_index=True,
)
chunks = text_splitter.split_documents(documents)

# 创建向量存储
vectorstore = FAISS.from_documents(chunks, embeddings)

# 创建检索链
qa_chain = RetrievalQA.from_chain_type(
    llm=llm,
    chain_type="stuff",
    retriever=vectorstore.as_retriever(search_kwargs={"k": 5}),
    return_source_documents=True,
    verbose=True
)

# 查询示例
query = "What is the change of free cash flow and what is the rate from the financial and operational highlights?"
query = "how many COVID-19 response initiatives (In millions) in year 2021?"
query = "After the year of COVID-19, how much EBITDA profit improved?"

response = qa_chain.invoke({"query": query})

print("\n************LangChain Query Response************")
print("Answer:", response["result"])
print("\nSource Documents:")
for i, doc in enumerate(response["source_documents"], 1):
    print(f"\nDocument {i}:")
    print(doc.page_content[:200] + "...")


query = "how many COVID-19 response initiatives (In millions) in year 2022?"

response = qa_chain.invoke({"query": query})

print("\n************LangChain Query Response************")
print("Answer:", response["result"])
print("\nSource Documents:")
for i, doc in enumerate(response["source_documents"], 1):
    print(f"\nDocument {i}:")
    print(doc.page_content[:200] + "...")


query = "how many COVID-19 response initiatives (In millions) in year 2023?"

response = qa_chain.invoke({"query": query})

print("\n************LangChain Query Response************")
print("Answer:", response["result"])
print("\nSource Documents:")
for i, doc in enumerate(response["source_documents"], 1):
    print(f"\nDocument {i}:")
    print(doc.page_content[:200] + "...")