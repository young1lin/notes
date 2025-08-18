import os
from dotenv import load_dotenv
from langchain_openai import ChatOpenAI
from langchain_openai import OpenAIEmbeddings
from langchain_core.documents import Document
from langchain_core.prompts import ChatPromptTemplate
from langchain_community.vectorstores import Chroma # pip install chromadb
from langchain_community.retrievers import BM25Retriever # pip install rank_bm25

battle_logs = [
    "猢狲身披锁子甲。",
    "猢狲在无回谷遭遇了妖怪，妖怪开始攻击，猢狲使用铜云棒抵挡。",
    "猢狲施展烈焰拳击退妖怪随后开启金刚体抵挡神兵攻击。",
    "妖怪使用寒冰箭攻击猢狲但被烈焰拳反击击溃。",
    "猢狲召唤烈焰拳与毁灭咆哮击败妖怪随后收集妖怪精华。"
]
request = "猢狲有什么装备和招数？"

bm25_retriever = BM25Retriever.from_texts(battle_logs)
bm25_response = bm25_retriever.invoke(request)
print(f"BM25检索结果：\n{bm25_response}")

docs = [Document(page_content=log) for log in battle_logs]
load_dotenv()
chroma_vs = Chroma.from_documents(
    docs,
    embedding=OpenAIEmbeddings(
        model="text-embedding-3-small",
        api_key=os.getenv("O3_API_KEY"),
        base_url=os.getenv("O3_BASE_URL")
        )
    )
chroma_retriever = chroma_vs.as_retriever()
chroma_response = chroma_retriever.invoke(request)
print(f"Chroma检索结果：\n{chroma_response}")

# hybrid_response = list({doc.page_content for doc in bm25_response}) # 缺锁子甲
# hybrid_response = list({doc.page_content for doc in chroma_response}) # 缺铜云棒
hybrid_response = list({doc.page_content for doc in bm25_response + chroma_response}) 
print(f"混合检索结果：\n{hybrid_response}")
prompt = ChatPromptTemplate.from_template("""
                基于以下上下文，回答问题。如果上下文中没有相关信息，
                请说"我无法从提供的上下文中找到相关信息"。
                上下文: {context}
                问题: {question}
                回答:"""
                                          )
llm = ChatOpenAI(
    model="gpt-4o",
    api_key=os.getenv("O3_API_KEY"),
    base_url=os.getenv("O3_BASE_URL"))
doc_content = "\n\n".join(hybrid_response)
answer = llm.invoke(prompt.format(question=request, context=doc_content))
print(f"LLM回答：\n{answer.content}")