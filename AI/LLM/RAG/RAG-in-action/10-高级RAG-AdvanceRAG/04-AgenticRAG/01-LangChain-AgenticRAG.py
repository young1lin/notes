import os
import getpass
from typing import Annotated, Sequence, TypedDict, List, Literal
from pprint import pprint

from langchain_community.document_loaders import WebBaseLoader
from langchain_community.vectorstores import Chroma
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain.tools.retriever import create_retriever_tool
from langchain_core.messages import BaseMessage, HumanMessage
from langchain_core.prompts import PromptTemplate
from pydantic import BaseModel, Field
from langchain_core.output_parsers import StrOutputParser
from langchain import hub
from langchain_community.tools.tavily_search import TavilySearchResults
from langgraph.graph import END, StateGraph, START
from langgraph.prebuilt import ToolNode, tools_condition

# ----- 1. 设置 API 密钥 -----
def _set_env(key: str):
    if key not in os.environ:
        os.environ[key] = getpass.getpass(f"{key}: ")
_set_env("OPENAI_API_KEY")

# ----- 2. 构建检索器并创建检索工具 -----
urls = [
    "https://lilianweng.github.io/posts/2023-06-23-agent/",
    # "https://lilianweng.github.io/posts/2023-03-15-prompt-engineering/",
    # "https://lilianweng.github.io/posts/2023-10-25-adv-attack-llm/",
]
# 加载文档
docs = [WebBaseLoader(url).load() for url in urls]
docs_list = [item for sublist in docs for item in sublist]
# 分块
splitter = RecursiveCharacterTextSplitter.from_tiktoken_encoder(chunk_size=100, chunk_overlap=50)
doc_splits = splitter.split_documents(docs_list)
# 向量存储
vectorstore = Chroma.from_documents(documents=doc_splits,
                                   collection_name="rag-chroma",
                                   embedding=OpenAIEmbeddings())
retriever = vectorstore.as_retriever()
# 创建检索工具
retriever_tool = create_retriever_tool(
    retriever,
    "retrieve_blog_posts",
    "搜索并返回 Lilian Weng 博客中关于代理、提示工程和对抗攻击的信息。"
)
tools = [retriever_tool]

# ----- 3. 定义 Agent 状态 -----
class AgentState(TypedDict):
    messages: Sequence[BaseMessage]
    retrieval_done: bool
    graded: bool
    grade_result: str

# ----- 4. 节点函数定义 -----
# 文档相关性评分节点
def grade_documents(state: AgentState) -> AgentState:
    class Grade(BaseModel):
        binary_score: str = Field(description="相关性评分 'yes' or 'no'.")
    model = ChatOpenAI(temperature=0, model="gpt-4o", streaming=True)
    grader = PromptTemplate(
        template="""
你是一个相关性评分器。
检索到的文档：\n{context}\n用户问题：{question}\n如果相关则返回 'yes'，否则 'no'.""",
        input_variables=["context","question"]
    ) | model.with_structured_output(Grade)
    msgs = state['messages']
    question = msgs[0].content
    docs = msgs[-1].content
    result = grader.invoke({"question": question, "context": docs})
    # 保存评分结果
    return {
        "messages": msgs,
        "retrieval_done": True,
        "graded": True,
        "grade_result": "generate" if result.binary_score == "yes" else "rewrite"
    }

# 条件路由：评估文档相关性
def route_after_grading(state: AgentState) -> str:
    if state.get("grade_result") == "generate":
        return "generate"
    else:
        return "rewrite"

# 自定义检索节点，合并历史消息
def retrieve(state: AgentState) -> AgentState:
    msgs = state['messages']
    tool = tools[0]  # 这里只有一个检索工具
    question = msgs[0].content
    docs = tool.invoke(question)
    retrieval_msg = HumanMessage(content=docs)
    return {
        "messages": msgs + [retrieval_msg],
        "retrieval_done": True,
        "graded": False,
        "grade_result": ""
    }

# Agent决策节点
def agent(state: AgentState) -> AgentState:
    model = ChatOpenAI(temperature=0, model="gpt-4o", streaming=True)
    model = model.bind_tools(tools)
    msgs = state.get('messages') or []
    if not msgs:
        raise ValueError("agent节点调用时消息列表为空，无法生成回复。请检查上游节点输出。")
    
    # 添加系统消息来指导使用检索工具
    system_msg = HumanMessage(content="请使用检索工具来回答问题。")
    response = model.invoke([system_msg] + msgs)
    return {
        "messages": msgs + [response],
        "retrieval_done": False,
        "graded": False,
        "grade_result": ""
    }

# 条件路由：决定是使用工具还是结束
def should_use_tools(state: AgentState) -> str:
    msgs = state['messages']
    last_msg = msgs[-1]
    # 检查消息内容是否包含工具调用或是否需要检索
    if (hasattr(last_msg, "tool_calls") and last_msg.tool_calls) or \
       (isinstance(last_msg.content, str) and "retrieve" in last_msg.content.lower()):
        return "retrieve"
    return "end"

# 重写查询节点
def rewrite(state: AgentState) -> AgentState:
    msgs = state['messages']
    question = msgs[0].content
    prompt = HumanMessage(content=f"重写以下问题以更好检索文档：\n{question}\n")
    model = ChatOpenAI(temperature=0, model="gpt-4o", streaming=True)
    resp = model.invoke([prompt])
    return {
        "messages": [resp],  # 这里重置消息，只保留新问题
        "retrieval_done": False,
        "graded": False,
        "grade_result": ""
    }

# 生成回答节点
def generate(state: AgentState) -> AgentState:
    msgs = state['messages']
    question = msgs[0].content
    docs = msgs[-1].content
    rag_prompt = hub.pull("rlm/rag-prompt")
    llm = ChatOpenAI(model_name="gpt-4o", temperature=0, streaming=True)
    chain = rag_prompt | llm | StrOutputParser()
    answer = chain.invoke({"context": docs, "question": question})
    return {
        "messages": msgs + [HumanMessage(content=answer)],
        "retrieval_done": True,
        "graded": True,
        "grade_result": "generate"
    }

# ----- 5. 构建并编译图工作流 -----
wf = StateGraph(AgentState)
wf.add_node("agent", agent)
wf.add_node("retrieve", retrieve)
wf.add_node("grade_documents", grade_documents)
wf.add_node("rewrite", rewrite)
wf.add_node("generate", generate)
# 节点连接
wf.add_edge(START, "agent")
wf.add_conditional_edges("agent", should_use_tools, {"retrieve": "retrieve", "end": END})
wf.add_edge("retrieve", "grade_documents")
wf.add_conditional_edges("grade_documents", route_after_grading, {"generate": "generate", "rewrite": "rewrite"})
wf.add_edge("generate", END)
wf.add_edge("rewrite", "agent")

app = wf.compile()
# try:
#     # 先获取 PNG 二进制数据
#     from langchain_core.runnables.graph import MermaidDrawMethod
#     png_data = app.get_graph(xray=True).draw_mermaid_png(
#         draw_method=MermaidDrawMethod.PYPPETEER  # 使用本地浏览器渲染，无需外部服务
#     )

#     # 将二进制数据保存到当前目录下的 graph.png
#     with open("10-高级RAG-AdvanceRAG/04-AgenticRAG/AgenticRAG-Graph.png", "wb") as f:
#         f.write(png_data)

#     print("已保存为：AdaptiveRAG-Graph.png")
# except Exception as e:
#     print(f"保存图片时出错: {e}")

# ----- 6. 运行示例 -----
# 初始化输入消息，使用 HumanMessage 类型
from langchain_core.messages import HumanMessage
inputs = {
    "messages": [
        HumanMessage(content="智能体有哪些类型的记忆?")
    ],
    "retrieval_done": False,
    "graded": False,
    "grade_result": ""
}

# 运行并打印每个节点的输出
final_output = None
for output in app.stream(inputs):
    print("\n=== 节点输出 ===")
    # 打印每个节点的名称和状态
    for node_name, state in output.items():
        print(f"\n节点名称: {node_name}")
        if state and "messages" in state:
            print("最新消息:", state["messages"][-1].content)
        print(f"检索状态: {state.get('retrieval_done')}")
        print(f"评分状态: {state.get('graded')}")
        print(f"评分结果: {state.get('grade_result')}")
    print("===============")
    final_output = output

# 打印最终回答
if final_output:
    # 检查所有可能的最终节点
    final_state = None
    for node in ["generate", "agent"]:
        if node in final_output:
            final_state = final_output[node]
            break
            
    if final_state and "messages" in final_state:
        print("\n=== 最终回答 ===")
        print(final_state["messages"][-1].content)
        print("===============")
    else:
        print("\n=== 错误：状态中未找到消息 ===")
else:
    print("\n=== 错误：未能获取最终输出 ===")
