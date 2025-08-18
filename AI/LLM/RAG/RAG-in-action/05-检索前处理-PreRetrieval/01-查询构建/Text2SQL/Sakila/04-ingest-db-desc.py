# ingest_dbdesc.py
import logging
from pymilvus import MilvusClient, DataType, FieldSchema, CollectionSchema
from pymilvus import model
import torch
import yaml

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# 1. 初始化嵌入函数
embedding_function = model.dense.OpenAIEmbeddingFunction(model_name='text-embedding-3-large')

# 2. 加载 DB 描述（假设 db_description.yaml 格式为
#    table_name:
#       column_name: "业务含义"
#       ...
# )
with open("90-文档-Data/sakila/db_description.yaml", "r") as f:
    desc_map = yaml.safe_load(f)
    logging.info(f"[DBDESC] 从YAML文件加载了 {len(desc_map)} 个表的描述")

# 3. 连接 Milvus
client = MilvusClient("text2sql_milvus_sakila.db")

# 4. 定义 Collection Schema
vector_dim = len(embedding_function(["dummy"])[0])
fields = [
    FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
    FieldSchema(name="vector", dtype=DataType.FLOAT_VECTOR, dim=vector_dim),
    FieldSchema(name="table_name", dtype=DataType.VARCHAR, max_length=100),
    FieldSchema(name="column_name", dtype=DataType.VARCHAR, max_length=100),
    FieldSchema(name="description", dtype=DataType.VARCHAR, max_length=1000),
]
schema = CollectionSchema(fields, description="DB Description Knowledge Base", enable_dynamic_field=False)

# 5. 创建 Collection（如不存在）
collection_name = "dbdesc_knowledge"
if not client.has_collection(collection_name):
    client.create_collection(collection_name=collection_name, schema=schema)
    logging.info(f"[DBDESC] 创建了新的集合 {collection_name}")
else:
    logging.info(f"[DBDESC] 集合 {collection_name} 已存在")

# 6. 为向量字段添加索引
index_params = client.prepare_index_params()
index_params.add_index(field_name="vector", index_type="AUTOINDEX", metric_type="COSINE", params={"nlist": 1024})
client.create_index(collection_name=collection_name, index_params=index_params)

# 7. 批量插入描述
data = []
texts = []
for tbl, cols in desc_map.items():
    for col, desc in cols.items():
        texts.append(desc)
        data.append({"table_name": tbl, "column_name": col, "description": desc})

logging.info(f"[DBDESC] 准备处理 {len(data)} 个字段描述")

# 生成全部嵌入
embeddings = embedding_function(texts)
logging.info(f"[DBDESC] 成功生成了 {len(embeddings)} 个向量嵌入")

# 组织为 Milvus insert 格式
records = []
for emb, rec in zip(embeddings, data):
    rec["vector"] = emb
    records.append(rec)

res = client.insert(collection_name=collection_name, data=records)
logging.info(f"[DBDESC] 成功插入了 {len(records)} 条记录到Milvus")
logging.info(f"[DBDESC] 插入结果: {res}")

logging.info("[DBDESC] 知识库构建完成")
