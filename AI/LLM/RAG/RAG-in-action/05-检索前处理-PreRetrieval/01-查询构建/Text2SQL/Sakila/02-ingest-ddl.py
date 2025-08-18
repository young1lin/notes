# ingest_ddl.py
import logging
from pymilvus import MilvusClient, DataType, FieldSchema, CollectionSchema
from pymilvus import model
import torch
import yaml

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# 1. 初始化嵌入函数
embedding_function = model.dense.OpenAIEmbeddingFunction(model_name='text-embedding-3-large')

# 2. 读取 DDL 列表（假设 ddl_statements.yaml 中存放 {table_name: "CREATE TABLE ..."}）
with open("90-文档-Data/sakila/ddl_statements.yaml","r") as f:
    ddl_map = yaml.safe_load(f)
    logging.info(f"[DDL] 从YAML文件加载了 {len(ddl_map)} 个表/视图定义")

# 3. 连接 Milvus
client = MilvusClient("text2sql_milvus_sakila.db")

# 4. 定义 Collection Schema
#    字段：id, vector, table_name, ddl_text
vector_dim = len(embedding_function(["dummy"])[0])
fields = [
    FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
    FieldSchema(name="vector", dtype=DataType.FLOAT_VECTOR, dim=vector_dim),
    FieldSchema(name="table_name", dtype=DataType.VARCHAR, max_length=100),
    FieldSchema(name="ddl_text", dtype=DataType.VARCHAR, max_length=2000),
]
schema = CollectionSchema(fields, description="DDL Knowledge Base", enable_dynamic_field=False)

# 5. 创建 Collection（如不存在）
collection_name = "ddl_knowledge"
if not client.has_collection(collection_name):
    client.create_collection(collection_name=collection_name, schema=schema)
    logging.info(f"[DDL] 创建了新的集合 {collection_name}")
else:
    logging.info(f"[DDL] 集合 {collection_name} 已存在")

# 6. 为向量字段添加索引
index_params = client.prepare_index_params()
index_params.add_index(field_name="vector", index_type="AUTOINDEX", metric_type="COSINE", params={"nlist": 1024})
client.create_index(collection_name=collection_name, index_params=index_params)

# 7. 批量插入 DDL
data = []
texts = []
for tbl, ddl in ddl_map.items():
    texts.append(ddl)
    data.append({"table_name": tbl, "ddl_text": ddl})

logging.info(f"[DDL] 准备处理 {len(data)} 个表/视图的DDL语句")

# 生成全部嵌入
embeddings = embedding_function(texts)
logging.info(f"[DDL] 成功生成了 {len(embeddings)} 个向量嵌入")

# 组织为 Milvus insert 格式
records = []
for emb, rec in zip(embeddings, data):
    rec["vector"] = emb
    records.append(rec)

res = client.insert(collection_name=collection_name, data=records)
logging.info(f"[DDL] 成功插入了 {len(records)} 条记录到Milvus")
logging.info(f"[DDL] 插入结果: {res}")

logging.info("[DDL] 知识库构建完成")
