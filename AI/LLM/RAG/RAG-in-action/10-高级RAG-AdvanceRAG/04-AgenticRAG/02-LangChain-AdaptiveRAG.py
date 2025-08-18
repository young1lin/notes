import os
import getpass
from typing import Literal, List
from pprint import pprint

from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import WebBaseLoader
from langchain_community.vectorstores import Chroma
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from pydantic import BaseModel, Field
from langchain.schema import Document
from langchain import hub
from langchain_core.output_parsers import StrOutputParser
from langchain_community.tools.tavily_search import TavilySearchResults
from langgraph.graph import END, StateGraph, START

# ----- 0. 安装依赖 -----
# pip install -U langchain_community tiktoken langchain-openai langchain-cohere \
#         langchainhub chromadb langchain langgraph tavily-python

# ----- 1. 设置 API 密钥 -----
def _set_env(var: str):
    if not os.environ.get(var):
        os.environ[var] = getpass.getpass(f"{var}: ")

_set_env("OPENAI_API_KEY")
_set_env("COHERE_API_KEY")
_set_env("TAVILY_API_KEY")

# ----- 2. 构建向量索引 -----
# 2.1 Embedding 模型
embd = OpenAIEmbeddings()
# 2.2 文档来源 URL 列表
urls = [
    "https://lilianweng.github.io/posts/2023-06-23-agent/",
    # "https://lilianweng.github.io/posts/2023-03-15-prompt-engineering/",
    # "https://lilianweng.github.io/posts/2023-10-25-adv-attack-llm/",
]
# 2.3 加载并拆分文档
docs = [WebBaseLoader(url).load() for url in urls]
docs_list = [d for sub in docs for d in sub]
splitter = RecursiveCharacterTextSplitter.from_tiktoken_encoder(chunk_size=500, chunk_overlap=0)
doc_splits = splitter.split_documents(docs_list)
# 2.4 添加到 Chroma 向量存储
vectorstore = Chroma.from_documents(documents=doc_splits, collection_name="rag-chroma", embedding=embd)
retriever = vectorstore.as_retriever()

# ----- 3. 查询路由模型 -----
class RouteQuery(BaseModel):
    """根据问题路由到向量存储或网络搜索。"""
    datasource: Literal["vectorstore", "web_search"] = Field(
        ..., description="根据问题选择 'vectorstore' 或 'web_search'."
    )

llm_router = ChatOpenAI(model="gpt-4o", temperature=0)
structured_router = llm_router.with_structured_output(RouteQuery)
route_prompt = ChatPromptTemplate.from_messages([
    ("system", 
     "你是一个查询路由专家。向量存储包含与代理、提示工程和对抗攻击相关的文档。"),
    ("human", "{question}")
])
question_router = route_prompt | structured_router

# ----- 4. LLM 评分器: 文档相关性 -----
class GradeDocuments(BaseModel):
    """检索文档的相关性评分: 'yes' 或 'no'."""
    binary_score: str = Field(description="相关: 'yes'|'no'.")

llm_doc_grader = ChatOpenAI(model="gpt-4o", temperature=0)
structured_doc_grader = llm_doc_grader.with_structured_output(GradeDocuments)
doc_grade_prompt = ChatPromptTemplate.from_messages([
    ("system", 
     "你是相关性评分器。文档包含与问题相关的关键词或语义即视为相关。返回 'yes' 或 'no'."),
    ("human", "Retrieved document:\n\n{document}\n\nUser question: {question}")
])
retrieval_grader = doc_grade_prompt | structured_doc_grader

# ----- 5. LLM 评分器: 幻觉检测 -----
class GradeHallucinations(BaseModel):
    """检测回答是否基于事实: 'yes'|'no'."""
    binary_score: str = Field(description="基于事实: 'yes'|'no'.")

llm_hallu_grader = ChatOpenAI(model="gpt-4o", temperature=0)
structured_hallu = llm_hallu_grader.with_structured_output(GradeHallucinations)
hallu_prompt = ChatPromptTemplate.from_messages([
    ("system", "你是幻觉检测器。判断回答是否基于提供的事实。返回 'yes' 或 'no'."),
    ("human", "Set of facts:\n\n{documents}\n\nLLM generation: {generation}")
])
hallucination_grader = hallu_prompt | structured_hallu

# ----- 6. LLM 评分器: 回答完整性 -----
class GradeAnswer(BaseModel):
    """判断回答是否回答了问题: 'yes'|'no'."""
    binary_score: str = Field(description="回答完整: 'yes'|'no'.")

llm_ans_grader = ChatOpenAI(model="gpt-4o", temperature=0)
structured_ans = llm_ans_grader.with_structured_output(GradeAnswer)
ans_prompt = ChatPromptTemplate.from_messages([
    ("system", "你是回答质量评分器。判断回答是否解决了问题。返回 'yes' 或 'no'."),
    ("human", "User question:\n\n{question}\n\nLLM generation: {generation}")
])
answer_grader = ans_prompt | structured_ans

# ----- 7. 问题重写器 -----
from langchain.schema import Document

question_rewriter_llm = ChatOpenAI(model="gpt-4o", temperature=0)
re_write_prompt = ChatPromptTemplate.from_messages([
    ("system", 
     "将输入问题重写为更适合向量检索的版本，保持语义意图。"),
    ("human", "Here is the initial question:\n\n{question}\nFormulate an improved question.")
])
from langchain_core.output_parsers import StrOutputParser
question_rewriter = re_write_prompt | question_rewriter_llm | StrOutputParser()

# ----- 8. Web 搜索工具 -----
web_search_tool = TavilySearchResults(k=3)

# ----- 9. 构建图状态和工作流节点 -----
class GraphState(BaseModel):
    question: str
    generation: str = ""
    documents: List[Document] = []

    class Config:
        arbitrary_types_allowed = True

# 检索节点
def retrieve_node(state: GraphState):
    question = state.question
    docs = retriever.get_relevant_documents(question)
    return {"documents": docs, "question": question}

# 文档评分和过滤
def grade_docs_node(state: GraphState):
    question = state.question
    docs = state.documents
    filtered = []
    for d in docs:
        score = retrieval_grader.invoke({"question": question, "document": d.page_content})
        if score.binary_score == "yes":
            filtered.append(d)
    return {"documents": filtered, "question": question}

# 问题重写节点
def transform_query_node(state: GraphState):
    better_q = question_rewriter.invoke({"question": state.question})
    return {"documents": state.documents, "question": better_q}

# Web 搜索节点
def web_search_node(state: GraphState):
    results = web_search_tool.invoke({"query": state.question})
    content = "\n".join([r["content"] for r in results])
    return {"documents": [Document(page_content=content)], "question": state.question}

# 回答生成节点
prompt = hub.pull("rlm/rag-prompt")
rag_chain = prompt | ChatOpenAI(model_name="gpt-3.5-turbo", temperature=0) | StrOutputParser()

def generate_node(state: GraphState):
    docs = state.documents
    if not docs:
        gen = "未检索到相关文档，无法生成回答。"
    else:
        gen = rag_chain.invoke({"context": docs, "question": state.question})
    return {"generation": gen, "documents": docs, "question": state.question}

# 幻觉与回答评估节点
def grade_generation_node(state: GraphState):
    # 幻觉检测
    h_score = hallucination_grader.invoke({"documents": state.documents, "generation": state.generation})
    if h_score.binary_score != "yes":
        return {"decision": "retry"}
    # 回答质量
    a_score = answer_grader.invoke({"question": state.question, "generation": state.generation})
    return {"decision": "end" if a_score.binary_score == "yes" else "rewrite"}

# ----- 10. 定义和编译工作流 -----
wf = StateGraph(GraphState)
wf.add_node("retrieve", retrieve_node)
wf.add_node("grade_documents", grade_docs_node)
wf.add_node("transform_query", transform_query_node)
wf.add_node("web_search", web_search_node)
wf.add_node("generate", generate_node)

# 路由条件
wf.add_conditional_edges(
    START,
    lambda s: question_router.invoke({"question": s.question}).datasource,
    {"vectorstore": "retrieve", "web_search": "web_search"}
)
# 检索后过滤或重写
wf.add_edge("retrieve", "grade_documents")
wf.add_conditional_edges(
    "grade_documents",
    lambda s: "transform_query" if not s.documents else "generate",
    {"transform_query": "transform_query", "generate": "generate"}
)
# 文档重写后返回检索
wf.add_edge("transform_query", "retrieve")
# Web 搜索直达生成
wf.add_edge("web_search", "generate")
# 生成后评估
wf.add_conditional_edges(
    "generate",
    lambda s: grade_generation_node(s)["decision"],
    {"retry": "generate", "rewrite": "transform_query", "end": END}
)

app = wf.compile()
try:
    # 先获取 PNG 二进制数据
    from langchain_core.runnables.graph import MermaidDrawMethod
    png_data = app.get_graph(xray=True).draw_mermaid_png(
        draw_method=MermaidDrawMethod.PYPPETEER  # 使用本地浏览器渲染，无需外部服务
    )

    # 将二进制数据保存到当前目录下的 graph.png
    with open("10-高级RAG-AdvanceRAG/04-AgenticRAG/AdaptiveRAG-Graph.png", "wb") as f:
        f.write(png_data)

    print("已保存为：AdaptiveRAG-Graph.png")
except Exception as e:
    print(f"保存图片时出错: {e}")


# ----- 11. 运行示例 -----
for q in [
    # "Who is the president of the United States?",
    "智能体有哪些类型的记忆?"
]:
    print("\n" + "="*50)
    print(f"问题: {q}")
    print("="*50)
    
    result = app.invoke({"question": q})
    print("\n[生成的回答]")
    print("-"*30)
    print(result["generation"])
    print("-"*30)
    
    print("\n" + "="*50 + "\n")
