"""
使用Ollama本地运行大语言模型，无需OpenAI API密钥。

1. 安装Ollama Server:
   - Windows: 访问 https://ollama.com/download 下载安装包
   - Linux/Mac: 运行 curl -fsSL https://ollama.com/install.sh | sh

2. 下载并运行模型:
   - 打开终端，运行以下命令下载模型:
     ollama pull qwen:7b  # 下载通义千问7B模型
     # 或
     ollama pull llama2:7b  # 下载Llama2 7B模型
     # 或
     ollama pull mistral:7b  # 下载Mistral 7B模型

3. 设置环境变量:
   - 在.env文件中添加:
     OLLAMA_MODEL=qwen:7b  # 或其他已下载的模型名称
"""

# 第一行代码：导入相关的库
from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from llama_index.embeddings.huggingface import HuggingFaceEmbedding
from llama_index.llms.ollama import Ollama # 需要pip install llama-index-llms-ollama
from dotenv import load_dotenv
import os

# 加载环境变量
load_dotenv()

# 加载本地嵌入模型
embed_model = HuggingFaceEmbedding(model_name="BAAI/bge-small-zh")

# 创建 Ollama LLM, 默认URL：http://localhost:11434
llm = Ollama(
    model=os.getenv("OLLAMA_MODEL"),
    request_timeout=300.0
)

# 第二行代码：加载数据
documents = SimpleDirectoryReader(input_files=["90-文档-Data/黑悟空/设定.txt"]).load_data() 

# 第三行代码：构建索引
index = VectorStoreIndex.from_documents(
    documents,
    embed_model=embed_model
)

# 第四行代码：创建问答引擎
query_engine = index.as_query_engine(
    llm=llm
)

# 第五行代码: 开始问答
print(query_engine.query("黑神话悟空中有哪些战斗工具?"))
