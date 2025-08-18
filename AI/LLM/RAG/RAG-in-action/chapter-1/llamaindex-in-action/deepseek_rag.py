from config import BLACK_WUKONG_DATA_FILE_PATH

# 导入相关的库
from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from llama_index.embeddings.huggingface import (
    HuggingFaceEmbedding,
)  # 需要pip install llama-index-embeddings-huggingface
from llama_index.llms.deepseek import (
    DeepSeek,
)  # 需要pip install llama-index-llms-deepseek

from llama_index.core import Settings  # 可以看看有哪些Setting

# https://docs.llamaindex.ai/en/stable/examples/llm/deepseek/
# Settings.llm = DeepSeek(model="deepseek-chat")
# Settings.embed_model = HuggingFaceEmbedding(model="BAAI/bge-small-zh")
# Settings.llm = OpenAI(model="gpt-3.5-turbo")
# Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# 加载环境变量
from dotenv import load_dotenv
import os


def main():
    print("开始运行")
    # 加载本地嵌入模型
    embed_model = HuggingFaceEmbedding(
        model_name="BAAI/bge-small-zh"  # 模型路径和名称（首次执行时会从HuggingFace下载）
    )

    # 加载 .env 文件中的环境变量
    load_dotenv()

    # 创建 Deepseek LLM（通过API调用最新的DeepSeek大模型）
    llm = DeepSeek(
        model="deepseek-chat",  # 使用最新的推理模型R1
    )
    print("加载环境变量完成")

    # 加载数据
    documents = SimpleDirectoryReader(
        input_files=[BLACK_WUKONG_DATA_FILE_PATH]
    ).load_data()
    print("加载数据完成")
    # 构建索引
    index = VectorStoreIndex.from_documents(
        documents,
        embed_model=embed_model,
        # llm=llm  # 设置构建索引时的语言模型（一般不需要）
    )
    print("构建索引完成")
    # 创建问答引擎
    query_engine = index.as_query_engine(llm=llm)  # 设置生成模型
    print("创建问答引擎完成")
    # 开始问答
    print(query_engine.query("黑神话悟空中有哪些战斗工具?"))


if __name__ == "__main__":
    main()

a = lambda x: x + 1

print(a(1))