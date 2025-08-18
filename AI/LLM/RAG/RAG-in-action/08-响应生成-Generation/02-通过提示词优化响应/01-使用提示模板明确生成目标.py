from langchain.prompts import PromptTemplate
from langchain_community.document_loaders import TextLoader
from langchain.text_splitter import CharacterTextSplitter
from langchain_community.embeddings import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from langchain_openai import OpenAI
import os

# 1. 加载文档
loader = TextLoader("90-文档-Data/黑悟空/设定.txt")
documents = loader.load()

# 2. 分割文档
text_splitter = CharacterTextSplitter(chunk_size=1000, chunk_overlap=200)
texts = text_splitter.split_documents(documents)

# 3. 创建向量数据库
embeddings = OpenAIEmbeddings(openai_api_key=os.getenv("OPENAI_API_KEY"))
db = FAISS.from_documents(texts, embeddings)

# 4. 检索相关内容
query = "白骨精的特点和战斗方式是什么？"
docs = db.similarity_search(query)
retrieved_content = docs[0].page_content

# 5. 定义提示模板
template = """
基于以下检索到的资料：
{context}

请详细分析并按照以下格式生成角色分析报告：

人物名称：[提供完整名称]

背景故事：介绍角色的来历和背景，与其他角色的关系，在故事中的定位。
技能特点：介绍角色的主要技能和能力，特殊能力描述，战斗风格特点。
战斗策略：介绍角色的主要攻击方式，防御机制，战斗中的特殊表现，克制和弱点。

请基于资料进行详尽分析，确保内容准确且具有连贯性。
"""

# 创建PromptTemplate和LLM
prompt = PromptTemplate(
    input_variables=["context"],
    template=template
)

llm = OpenAI(openai_api_key=os.getenv("OPENAI_API_KEY"))

# 生成文本
formatted_prompt = prompt.format(context=retrieved_content)
response = llm.invoke(formatted_prompt)
print(response)
