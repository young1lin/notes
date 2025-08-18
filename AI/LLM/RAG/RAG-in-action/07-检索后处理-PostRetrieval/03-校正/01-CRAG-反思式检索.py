"""
CRAG (Corrective Retrieval-Augmented Generation) 反思式检索系统

CRAG是一种改进的RAG方法，通过以下步骤提高检索质量：
1. 检索：从向量数据库检索相关文档
2. 评分：评估检索文档的相关性
3. 决策：根据评分结果决定是直接生成答案还是进行查询重写
4. 校正：如果文档不相关，重写查询并进行网络搜索
5. 生成：基于过滤后的相关文档生成最终答案

这种方法能够自动检测和纠正不准确的检索结果，提高RAG系统的可靠性。
"""

# ================================
# 第一部分：数据准备和向量数据库构建
# ================================

#1 为3篇博客文章创建索引
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import WebBaseLoader
from langchain_community.vectorstores import Chroma
from langchain_openai import OpenAIEmbeddings
from dotenv import load_dotenv

# 加载环境变量（包含OpenAI API密钥等）
load_dotenv()

# 定义要索引的博客文章URL
# 这些是关于AI智能体、提示工程和对抗攻击的技术博客
urls = [
    "https://lilianweng.github.io/posts/2023-06-23-agent/",        # AI智能体相关
    "https://lilianweng.github.io/posts/2023-03-15-prompt-engineering/",  # 提示工程
    "https://lilianweng.github.io/posts/2023-10-25-adv-attack-llm/",      # LLM对抗攻击
]

# 使用WebBaseLoader加载每个URL的内容
docs = [WebBaseLoader(url).load() for url in urls]
# 将嵌套列表展平为单一文档列表
docs_list = [item for sublist in docs for item in sublist]

# 创建文本分割器，使用tiktoken编码器来准确计算token数量
# chunk_size=250: 每个文档片段最多250个token
# chunk_overlap=0: 片段间无重叠，避免重复信息
text_splitter = RecursiveCharacterTextSplitter.from_tiktoken_encoder(
    chunk_size=250, chunk_overlap=0
)
# 将文档分割成小块，便于检索和处理
doc_splits = text_splitter.split_documents(docs_list)

# 创建向量数据库
# 使用Chroma作为向量存储，OpenAI的嵌入模型进行向量化
vectorstore = Chroma.from_documents(
    documents=doc_splits,
    collection_name="rag-chroma",  # 集合名称
    embedding=OpenAIEmbeddings(),  # 使用OpenAI的text-embedding-ada-002模型
)
# 将向量存储转换为检索器，用于后续的相似性搜索
retriever = vectorstore.as_retriever()

# ================================
# 第二部分：检索评分器 - CRAG的核心组件
# ================================

#2 检索评分器
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.pydantic_v1 import BaseModel, Field
from langchain_openai import ChatOpenAI

# 定义评分结果的数据模型
# 使用Pydantic确保输出格式的一致性和类型安全
class GradeDocuments(BaseModel):
    """对检索文档相关性的二元评分。
    
    这个类定义了文档相关性评分的输出格式，
    确保模型只返回'yes'或'no'的明确判断。
    """

    binary_score: str = Field(
        description="文档与问题相关为'yes'，不相关为'no'"
    )

# 创建具有结构化输出的语言模型
# temperature=0.5: 适中的随机性，平衡一致性和创造性
llm = ChatOpenAI(model="gpt-4o", temperature=0.5)
# 将模型输出限制为GradeDocuments格式
structured_llm_grader = llm.with_structured_output(GradeDocuments)

# 构建评分提示模板
# 系统提示定义了评分员的角色和评分标准
system = """你是一个评估检索文档与用户问题相关性的评分员。 \n 
    如果文档包含与问题相关的关键词或语义含义，则将其评为相关。 \n
    给出一个二元评分'yes'或'no'来表示文档是否与问题相关。"""

grade_prompt = ChatPromptTemplate.from_messages(
    [
        ("system", system),
        ("human", "检索到的文档: \n\n {document} \n\n 用户问题: {question}"),
    ]
)

# 创建检索评分链：提示模板 + 结构化语言模型
retrieval_grader = grade_prompt | structured_llm_grader

# 测试评分器
question = "agent memory"  # 测试问题：关于智能体记忆
docs = retriever.get_relevant_documents(question)  # 检索相关文档
doc_txt = docs[1].page_content  # 获取第二个文档的内容
# 打印评分结果，验证评分器是否正常工作
print(retrieval_grader.invoke({"question": question, "document": doc_txt}))

# ================================
# 第三部分：RAG生成链
# ================================

#3 设置生成模型
from langchain import hub
from langchain_core.output_parsers import StrOutputParser

# 从LangChain Hub获取预构建的RAG提示模板
# 这个模板专门设计用于基于上下文回答问题
prompt = hub.pull("rlm/rag-prompt")

# 创建用于生成答案的语言模型
# temperature=0: 确保输出的一致性和可重复性
llm = ChatOpenAI(model_name="gpt-3.5-turbo", temperature=0)

# 文档格式化函数
def format_docs(docs):
    """将文档列表格式化为单一字符串。
    
    Args:
        docs: 文档对象列表
        
    Returns:
        str: 用双换行符连接的文档内容字符串
    """
    return "\n\n".join(doc.page_content for doc in docs)

# 构建RAG生成链：提示模板 + 语言模型 + 字符串解析器
rag_chain = prompt | llm | StrOutputParser()

# 测试生成链
generation = rag_chain.invoke({"context": docs, "question": question})
print(generation)

# ================================
# 第四部分：查询重写器
# ================================

#4 设置问题重写器
# 创建用于查询重写的语言模型
llm = ChatOpenAI(model="gpt-4o", temperature=0.5)

# 查询重写的系统提示
# 目的是将模糊或不准确的查询重写为更适合搜索的形式
system = """你是一个问题重写者，将输入的问题转换为更适合网络搜索的版本。 \n 
     分析输入并尝试推理出潜在的语义意图/含义。"""

re_write_prompt = ChatPromptTemplate.from_messages(
    [
        ("system", system),
        (
            "human",
            "这是初始问题: \n\n {question} \n 请重新表述为一个改进的问题。",
        ),
    ]
)

# 创建查询重写链
question_rewriter = re_write_prompt | llm | StrOutputParser()
# 测试查询重写功能
question_rewriter.invoke({"question": question})

# ================================
# 第五部分：网络搜索工具
# ================================

#5 设置网络搜索工具
from langchain_community.tools.tavily_search import TavilySearchResults

# 创建网络搜索工具
# k=3: 返回最多3个搜索结果
web_search_tool = TavilySearchResults(k=3)

# ================================
# 第六部分：图状态定义
# ================================

#6 设置CRAG所需的导入
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.pydantic_v1 import BaseModel, Field
from langchain_core.runnables import RunnablePassthrough

#7 定义图状态
from typing import List 
from typing_extensions import TypedDict

class GraphState(TypedDict):
    """
    表示CRAG工作流图的状态。
    
    这个状态在整个CRAG流程中传递，包含了处理过程中的所有关键信息。

    属性:
        question: 用户的原始问题或重写后的问题
        generation: 语言模型生成的最终答案
        web_search: 标记是否需要进行网络搜索（"Yes"/"No"）
        documents: 检索到的文档列表（原始检索结果或网络搜索结果）
    """

    question: str        # 当前处理的问题
    generation: str      # 生成的答案
    web_search: str      # 是否需要网络搜索的标志
    documents: List[str] # 文档列表

# ================================
# 第七部分：CRAG工作流节点函数
# ================================

from langchain.schema import Document

def retrieve(state):
    """
    检索节点：从向量数据库检索相关文档
    
    这是CRAG流程的第一步，基于用户问题检索潜在相关的文档。

    参数:
        state (dict): 当前图状态，必须包含'question'键

    返回:
        state (dict): 更新后的状态，添加了'documents'键
    """
    print("---检索---")
    question = state["question"]

    # 使用向量检索器获取相关文档
    # 基于语义相似性返回最相关的文档片段
    documents = retriever.get_relevant_documents(question)
    return {"documents": documents, "question": question}

def generate(state):
    """
    生成节点：基于检索到的文档生成答案
    
    这是CRAG流程的最后一步，使用过滤后的相关文档生成最终答案。

    参数:
        state (dict): 当前图状态，包含question和documents

    返回:
        state (dict): 添加generation键的更新状态
    """
    print("---生成---")
    question = state["question"]
    documents = state["documents"]

    # 使用RAG链生成答案
    # 将文档作为上下文，结合问题生成回答
    generation = rag_chain.invoke({"context": documents, "question": question})
    return {"documents": documents, "question": question, "generation": generation}

def grade_documents(state):
    """
    文档评分节点：评估检索文档的相关性
    
    这是CRAG的核心创新，通过LLM评估每个检索文档是否真正相关。
    只保留相关文档，如果没有相关文档则标记需要网络搜索。

    参数:
        state (dict): 当前图状态

    返回:
        state (dict): 更新documents为过滤后的相关文档，设置web_search标志
    """

    print("---检查文档与问题的相关性---")
    question = state["question"]
    documents = state["documents"]

    # 初始化过滤结果
    filtered_docs = []           # 存储相关文档
    web_search = "No"           # 默认不需要网络搜索
    has_relevant_docs = False   # 是否有相关文档的标志
    
    # 对每个检索到的文档进行相关性评分
    for d in documents:
        score = retrieval_grader.invoke(
            {"question": question, "document": d.page_content}
        )
        grade = score.binary_score
        
        if grade == "yes":
            print("---评分: 文档相关---")
            filtered_docs.append(d)    # 保留相关文档
            has_relevant_docs = True   # 标记找到相关文档
        else:
            print("---评分: 文档不相关---")
            continue  # 跳过不相关文档
    
    # CRAG的关键逻辑：只有在没有任何相关文档时才进行网络搜索
    # 这避免了不必要的网络搜索，提高了效率
    if not has_relevant_docs:
        web_search = "Yes"
        
    return {"documents": filtered_docs, "question": question, "web_search": web_search}

def transform_query(state):
    """
    查询转换节点：重写查询以提高搜索质量
    
    当检索到的文档都不相关时，重写原始查询以获得更好的搜索结果。

    参数:
        state (dict): 当前图状态

    返回:
        state (dict): 用重写后的问题更新question键
    """

    print("---转换查询---")
    question = state["question"]
    documents = state["documents"]

    # 使用查询重写器生成改进的问题
    # 重写后的问题通常更具体、更适合搜索
    better_question = question_rewriter.invoke({"question": question})
    return {"documents": documents, "question": better_question}

def web_search(state):
    """
    网络搜索节点：获取外部信息补充
    
    当本地文档库无法提供相关信息时，通过网络搜索获取额外信息。

    参数:
        state (dict): 包含当前状态
            - question: 问题（可能是重写后的）
            - documents: 文档列表

    返回:
        state (dict): 在documents中追加网络搜索结果
    """

    print("---网络搜索---")
    question = state["question"]
    documents = state["documents"]

    # 使用Tavily搜索工具进行网络搜索
    search_results = web_search_tool.invoke(question)
    
    # 将搜索结果格式化为文档对象
    # 这样可以与本地检索的文档保持一致的格式
    search_results_str = "\n".join([str(result) for result in search_results])
    web_results = Document(page_content=search_results_str)
    documents.append(web_results)

    return {"documents": documents, "question": question}

# ================================
# 第八部分：条件边缘逻辑
# ================================

### 边缘处理函数

def decide_to_generate(state):
    """
    决策节点：确定下一步行动
    
    这是CRAG工作流的关键决策点：
    - 如果有相关文档：直接生成答案
    - 如果没有相关文档：转换查询并进行网络搜索

    参数:
        state (dict): 当前图状态

    返回:
        str: 下一个要执行的节点名称
    """

    print("---评估已评分文档---")
    state["question"]
    web_search = state["web_search"]  # 获取是否需要网络搜索的标志
    state["documents"]

    if web_search == "Yes":
        # 所有本地文档都被评为不相关
        # 需要重写查询并进行网络搜索以获取更好的信息
        print("---决策: 所有文档与问题都不相关，转换查询---")
        return "transform_query"
    else:
        # 找到了相关文档，可以直接生成答案
        print("---决策: 生成---")
        return "generate"

# ================================
# 第九部分：构建和编译CRAG工作流图
# ================================

#8 编译图
from langgraph.graph import END, StateGraph, START

# 创建状态图工作流
workflow = StateGraph(GraphState)

# 添加所有节点到工作流图
workflow.add_node("retrieve", retrieve)              # 检索节点
workflow.add_node("grade_documents", grade_documents) # 文档评分节点
workflow.add_node("generate", generate)              # 答案生成节点
workflow.add_node("transform_query", transform_query) # 查询转换节点
workflow.add_node("web_search_node", web_search)     # 网络搜索节点

# 构建工作流图的边缘连接
# 定义节点之间的执行顺序和条件跳转

# 1. 从开始节点到检索节点
workflow.add_edge(START, "retrieve")

# 2. 从检索到文档评分
workflow.add_edge("retrieve", "grade_documents")

# 3. 从文档评分到条件分支
# 根据decide_to_generate函数的返回值选择下一个节点
workflow.add_conditional_edges(
    "grade_documents",           # 源节点
    decide_to_generate,          # 决策函数
    {
        "transform_query": "transform_query",  # 如果需要网络搜索
        "generate": "generate",                # 如果有相关文档
    },
)

# 4. 查询转换后进行网络搜索
workflow.add_edge("transform_query", "web_search_node")

# 5. 网络搜索后生成答案
workflow.add_edge("web_search_node", "generate")

# 6. 生成答案后结束
workflow.add_edge("generate", END)

# 编译工作流图为可执行的应用
app = workflow.compile()

# ================================
# 第十部分：运行CRAG系统
# ================================

#9 使用图回答问题

from pprint import pprint

# 准备输入问题
# 第一个问题：关于智能体记忆类型（英文）
inputs = {"question": "What are the types of agent memory?"}

# 第二个问题示例（中文，被注释掉）
# inputs = {"question": "为何山西省旅游资源丰富?"}

print("=== CRAG 工作流执行过程 ===")

# 流式执行CRAG工作流
# stream方法允许我们观察每个节点的执行过程
for output in app.stream(inputs):
    for key, value in output.items():
        # 打印当前执行的节点名称
        pprint(f"节点 '{key}':")
        
        # 可选：打印每个节点的完整状态信息
        # 这对调试和理解工作流很有帮助
        # pprint(value["keys"], indent=2, width=80, depth=None)
    pprint("\n---\n")

print("=== 最终生成结果 ===")
# 打印最终生成的答案
pprint(value["generation"])

"""
CRAG工作流总结：

1. 检索(retrieve): 从向量数据库检索候选文档
2. 评分(grade_documents): 使用LLM评估文档相关性
3. 决策(decide_to_generate): 根据评分结果选择路径
4a. 直接路径: 如果有相关文档 → 生成答案
4b. 校正路径: 如果无相关文档 → 转换查询 → 网络搜索 → 生成答案

这种设计确保了系统能够自动检测和纠正检索错误，
显著提高了RAG系统的准确性和可靠性。
"""


