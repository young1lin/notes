# 导入相关的库
from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from llama_index.embeddings.huggingface import HuggingFaceEmbedding # 需要pip install llama-index-embeddings-huggingface
from llama_index.llms.deepseek import DeepSeek  # 需要pip install llama-index-llms-deepseek

from llama_index.core import Settings # 可以看看有哪些Setting
# https://docs.llamaindex.ai/en/stable/examples/llm/deepseek/
# Settings.llm = DeepSeek(model="deepseek-chat")
Settings.embed_model = HuggingFaceEmbedding("BAAI/bge-small-zh")
# Settings.llm = OpenAI(model="gpt-3.5-turbo")
# Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# 加载环境变量
from dotenv import load_dotenv
import os

# 加载 .env 文件中的环境变量
load_dotenv()

# 创建 Deepseek LLM（通过API调用最新的DeepSeek大模型）
llm = DeepSeek(
    model="deepseek-reasoner", # 使用最新的推理模型R1
    api_key=os.getenv("DEEPSEEK_API_KEY")  # 从环境变量获取API key
)

# 加载数据
documents = SimpleDirectoryReader(input_files=["90-文档-Data/黑悟空/设定.txt"]).load_data() 

# 构建索引
index = VectorStoreIndex.from_documents(
    documents,
    # llm=llm  # 设置构建索引时的语言模型（一般不需要）
)

# 创建问答引擎
query_engine = index.as_query_engine(
    llm=llm  # 设置生成模型
    )

# 开始问答
print(query_engine.query("黑神话悟空中有哪些战斗工具?"))