# 导入相关的库
from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from llama_index.embeddings.huggingface import HuggingFaceEmbedding # 需要pip install llama-index-embeddings-huggingface

# 加载本地嵌入模型
# import os
# os.environ['HF_ENDPOINT']= 'https://hf-mirror.com' # 如果万一被屏蔽，可以设置镜像
embed_model = HuggingFaceEmbedding(
    model_name="BAAI/bge-small-zh" # 模型路径和名称（首次执行时会从HuggingFace下载）
    )

# 加载数据
documents = SimpleDirectoryReader(input_files=["90-文档-Data/黑悟空/设定.txt"]).load_data() 

# 构建索引
index = VectorStoreIndex.from_documents(
    documents,
    embed_model=embed_model
)

# 创建问答引擎
query_engine = index.as_query_engine()

# 开始问答
print(query_engine.query("黑神话悟空中有哪些战斗工具?"))