from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import WebBaseLoader
from langchain_community.vectorstores import Chroma
from langchain_openai import OpenAIEmbeddings
from dotenv import load_dotenv
load_dotenv()   

# 定义要加载的URL列表
urls = [
    "https://lilianweng.github.io/posts/2023-06-23-agent/",
    "https://lilianweng.github.io/posts/2023-03-15-prompt-engineering/",
    "https://lilianweng.github.io/posts/2023-10-25-adv-attack-llm/",
]

# 加载文档
docs = [WebBaseLoader(url).load() for url in urls]
docs_list = [item for sublist in docs for item in sublist]

# 创建文本分割器
text_splitter = RecursiveCharacterTextSplitter.from_tiktoken_encoder(
    chunk_size=250, chunk_overlap=0
)
doc_splits = text_splitter.split_documents(docs_list)

# 添加到向量数据库
vectorstore = Chroma.from_documents(
    documents=doc_splits,
    collection_name="rag-chroma",
    embedding=OpenAIEmbeddings(),
)
retriever = vectorstore.as_retriever()

### 检索评分器 ###

from langchain_core.prompts import ChatPromptTemplate
from pydantic import BaseModel, Field
# from langchain_core.pydantic_v1 import BaseModel, Field
from langchain_openai import ChatOpenAI

# 数据模型
class GradeDocuments(BaseModel):
    """对检索文档进行相关性评分的二元评分模型"""

    binary_score: str = Field(
        description="文档是否与问题相关，'是'或'否'"
    )

# 配置LLM和函数调用
llm = ChatOpenAI(model="gpt-4o", temperature=0)
structured_llm_grader = llm.with_structured_output(GradeDocuments)

# 提示词
system = """你是一个评估检索文档与用户问题相关性的评分器。\n 
    这不需要是一个严格的测试。目标是过滤掉错误的检索结果。\n
    如果文档包含与用户问题相关的关键词或语义含义，则将其评为相关。\n
    给出'是'或'否'的二元评分，以表明文档是否与问题相关。"""
grade_prompt = ChatPromptTemplate.from_messages(
    [
        ("system", system),
        ("human", "检索到的文档: \n\n {document} \n\n 用户问题: {question}"),
    ]
)

# 检索评分器及简单测试
retrieval_grader = grade_prompt | structured_llm_grader
question = "agent memory"
docs = retriever.invoke(question)
doc_txt = docs[1].page_content
print(retrieval_grader.invoke({"question": question, "document": doc_txt}))

### 生成器 ###

from langchain import hub
from langchain_core.output_parsers import StrOutputParser

# 提示词
prompt = hub.pull("rlm/rag-prompt")

# LLM配置
llm = ChatOpenAI(model_name="gpt-4o", temperature=0)

# 后处理
def format_docs(docs):
    return "\n\n".join(doc.page_content for doc in docs)

# 构建RAG链
rag_chain = prompt | llm | StrOutputParser()

# 运行
generation = rag_chain.invoke({"context": docs, "question": question})
print(generation)

### 幻觉评分器 ###

# 数据模型
class GradeHallucinations(BaseModel):
    """对生成答案中是否存在幻觉进行二元评分"""

    binary_score: str = Field(
        description="答案是否基于事实，'是'或'否'"
    )

# LLM配置
llm = ChatOpenAI(model="gpt-4o", temperature=0)
structured_llm_grader = llm.with_structured_output(GradeHallucinations)

# 提示词
system = """你是一个评估LLM生成内容是否基于检索事实的评分器。\n 
     给出'是'或'否'的二元评分。'是'表示答案是基于/由事实支持的。"""
hallucination_prompt = ChatPromptTemplate.from_messages(
    [
        ("system", system),
        ("human", "事实集合: \n\n {documents} \n\n LLM生成内容: {generation}"),
    ]
)

hallucination_grader = hallucination_prompt | structured_llm_grader
hallucination_grader.invoke({"documents": docs, "generation": generation})

### 答案评分器 ###

# 数据模型
class GradeAnswer(BaseModel):
    """评估答案是否解决问题的二元评分"""

    binary_score: str = Field(
        description="答案是否解决问题，'是'或'否'"
    )

# LLM配置
llm = ChatOpenAI(model="gpt-4o", temperature=0)
structured_llm_grader = llm.with_structured_output(GradeAnswer)

# 提示词
system = """你是一个评估答案是否解决/回答问题的评分器。\n 
     给出'是'或'否'的二元评分。'是'表示答案解决了问题。"""
answer_prompt = ChatPromptTemplate.from_messages(
    [
        ("system", system),
        ("human", "用户问题: \n\n {question} \n\n LLM生成内容: {generation}"),
    ]
)

answer_grader = answer_prompt | structured_llm_grader
answer_grader.invoke({"question": question, "generation": generation})

### 问题重写器 ###

# LLM配置
llm = ChatOpenAI(model="gpt-3.5-turbo-0125", temperature=0)

# 提示词
system = """你是一个问题重写器，将输入问题转换为更适合向量存储检索的更好版本。\n 
     查看输入并尝试理解潜在的语义意图/含义。"""
re_write_prompt = ChatPromptTemplate.from_messages(
    [
        ("system", system),
        (
            "human",
            "这是初始问题: \n\n {question} \n 请提出一个改进的问题。",
        ),
    ]
)

question_rewriter = re_write_prompt | llm | StrOutputParser()
question_rewriter.invoke({"question": question})

from typing import List
from typing_extensions import TypedDict

class GraphState(TypedDict):
    """
    表示图的状态。

    属性:
        question: 问题
        generation: LLM生成内容
        documents: 文档列表
    """

    question: str
    generation: str
    documents: List[str]

### 节点定义 ###

def retrieve(state):
    """
    检索文档

    参数:
        state (dict): 当前图状态

    返回:
        state (dict): 添加了检索文档的新状态
    """
    print("---检索文档---")
    question = state["question"]

    # 使用新的 invoke 方法
    documents = retriever.invoke(question)
    return {"documents": documents, "question": question}

def generate(state):
    """
    生成答案

    参数:
        state (dict): 当前图状态

    返回:
        state (dict): 添加了LLM生成内容的新状态
    """
    print("---生成答案---")
    question = state["question"]
    documents = state["documents"]

    # RAG生成
    generation = rag_chain.invoke({"context": documents, "question": question})
    return {"documents": documents, "question": question, "generation": generation}

def grade_documents(state):
    """
    判断检索到的文档是否与问题相关。

    参数:
        state (dict): 当前图状态

    返回:
        state (dict): 更新后的文档列表，只包含相关文档
    """
    print("---检查文档与问题的相关性---")
    question = state["question"]
    documents = state["documents"]

    # 评分每个文档
    filtered_docs = []
    for d in documents:
        score = retrieval_grader.invoke(
            {"question": question, "document": d.page_content}
        )
        grade = score.binary_score
        if grade == "是":
            print("---评分: 文档相关---")
            filtered_docs.append(d)
        else:
            print("---评分: 文档不相关---")
            continue
    return {"documents": filtered_docs, "question": question}

def transform_query(state):
    """
    转换查询以产生更好的问题。

    参数:
        state (dict): 当前图状态

    返回:
        state (dict): 更新后的问题
    """
    print("---转换查询---")
    question = state["question"]
    documents = state["documents"]

    # 重写问题
    better_question = question_rewriter.invoke({"question": question})
    return {"documents": documents, "question": better_question}

### 边定义 ###

def decide_to_generate(state):
    """
    决定是生成答案还是重新生成问题。

    参数:
        state (dict): 当前图状态

    返回:
        str: 下一个节点的二元决策
    """
    print("---评估已评分的文档---")
    state["question"]
    filtered_documents = state["documents"]

    if not filtered_documents:
        # 所有文档都已被过滤检查相关性
        # 我们将重新生成一个新的查询
        print("---决策: 所有文档都与问题不相关，转换查询---")
        return "transform_query"
    else:
        # 我们有相关文档，所以生成答案
        print("---决策: 生成答案---")
        return "generate"

def grade_generation_v_documents_and_question(state):
    """
    判断生成内容是否基于文档并回答问题。

    参数:
        state (dict): 当前图状态

    返回:
        str: 下一个节点的决策
    """
    print("---检查幻觉---")
    question = state["question"]
    documents = state["documents"]
    generation = state["generation"]

    score = hallucination_grader.invoke(
        {"documents": documents, "generation": generation}
    )
    grade = score.binary_score

    # 检查幻觉
    if grade == "是":
        print("---决策: 生成内容基于文档---")
        # 检查问答
        print("---评估生成内容与问题---")
        score = answer_grader.invoke({"question": question, "generation": generation})
        grade = score.binary_score
        if grade == "是":
            print("---决策: 生成内容回答了问题---")
            return "useful"
        else:
            print("---决策: 生成内容未回答问题---")
            return "not useful"
    else:
        print("---决策: 生成内容未基于文档，重试---")
        return "not supported"

from langgraph.graph import END, StateGraph, START

# 创建工作流图
workflow = StateGraph(GraphState)

# 定义节点
workflow.add_node("retrieve", retrieve)  # 检索
workflow.add_node("grade_documents", grade_documents)  # 评分文档
workflow.add_node("generate", generate)  # 生成
workflow.add_node("transform_query", transform_query)  # 转换查询

# 构建图
workflow.add_edge(START, "retrieve")
workflow.add_edge("retrieve", "grade_documents")
workflow.add_conditional_edges(
    "grade_documents",
    decide_to_generate,
    {
        "transform_query": "transform_query",
        "generate": "generate",
    },
)
workflow.add_edge("transform_query", "retrieve")
workflow.add_conditional_edges(
    "generate",
    grade_generation_v_documents_and_question,
    {
        "not supported": "generate",
        "useful": END,
        "not useful": "transform_query",
    },
)

# 编译
app = workflow.compile()
# try:
#     # 先获取 PNG 二进制数据
#     png_data = app.get_graph(xray=True).draw_mermaid_png()

#     # 将二进制数据保存到当前目录下的 graph.png
#     with open("08-响应生成-Generation/04-动态生成优化策略/graph.png", "wb") as f:
#         f.write(png_data)

#     print("已保存为：graph.png")
# except Exception as e:
#     print(f"保存图片时出错: {e}")

# from pprint import pprint

# # 运行示例1
# inputs = {"question": "解释不同类型的智能体记忆是如何工作的？"}
# for output in app.stream(inputs):
#     for key, value in output.items():
#         # 节点
#         pprint(f"节点 '{key}':")
#     pprint("\n---\n")

# # 最终生成
# pprint(value["generation"])

# # 运行示例2
# inputs = {"question": "解释思维链提示是如何工作的？"}
# for output in app.stream(inputs):
#     for key, value in output.items():
#         # 节点
#         pprint(f"节点 '{key}':")
#     pprint("\n---\n")

# # 最终生成
# pprint(value["generation"])
