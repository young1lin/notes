# 准备示例数据集
import pandas as pd
data_records = [
    {
        "monster_id": "BM001",
        "monster_name": "虎先锋",
        "location": "竹林关隘",
        "difficulty": "High",
        "synonyms": "猛虎妖, 虎妖",
        "description": "在竹林关卡中出现的猛虎型妖怪，力量强大。"
    },
    {
        "monster_id": "BM002",
        "monster_name": "火猿",
        "location": "火山洞窟",
        "difficulty": "Low",
        "synonyms": "烈焰猿, 炎猿",
        "description": "生活在火山洞窟的猿类妖怪，只是插科打诨的小兵。"
    },]
df = pd.DataFrame(data_records)

# 建立/连接Milvus
from pymilvus import MilvusClient, DataType, FieldSchema, CollectionSchema
from pymilvus import model
db_path = "./wukong.db"
client = MilvusClient(db_path)
collection_name = "Wukong_Monsters"

# 获取嵌入模型的向量维度
from pymilvus.model.dense import SentenceTransformerEmbeddingFunction 
embedding_function = SentenceTransformerEmbeddingFunction(model_name='BAAI/bge-large-zh')
sample_embedding = embedding_function(["示例文本"])[0]
vector_dim = len(sample_embedding)

# 定义集合模式并创建集合
fields = [
    FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
    FieldSchema(name="vector", dtype=DataType.FLOAT_VECTOR, dim=vector_dim),
    FieldSchema(name="monster_id", dtype=DataType.VARCHAR, max_length=50),
    FieldSchema(name="monster_name", dtype=DataType.VARCHAR, max_length=100),
    FieldSchema(name="location", dtype=DataType.VARCHAR, max_length=100),
    FieldSchema(name="difficulty", dtype=DataType.VARCHAR, max_length=20),
    FieldSchema(name="synonyms", dtype=DataType.VARCHAR, max_length=200),
    FieldSchema(name="description", dtype=DataType.VARCHAR, max_length=500),
]
schema = CollectionSchema(fields, description=" Wukong Monsters", enable_dynamic_field=True)
if not client.has_collection(collection_name):
    client.create_collection(collection_name=collection_name, schema=schema)

# 创建索引
index_params = client.prepare_index_params()
index_params.add_index(
    field_name="vector",
    index_type="AUTOINDEX",
    metric_type="L2",
    params={"nlist": 1024}
)
client.create_index(
    collection_name=collection_name, 
    index_params=index_params
)

# 批量插入数据
from tqdm import tqdm
for start_idx in tqdm(range(0, len(df)), desc="插入数据"):
    row = df.iloc[start_idx]    
    # 准备向量文本
    doc_parts = [str(row['monster_name'])]
    if row['synonyms']:
        doc_parts.append(f"(别名：{row['synonyms']})")
    if row['location']:
        doc_parts.append(f"场景：{row['location']}")
    if row['description']:
        doc_parts.append(f"描述：{row['description']}")
    doc_text = "；".join(doc_parts)    
    # 生成向量并插入数据
    embedding = embedding_function([doc_text])[0]
    data_to_insert = [{
        "vector": embedding,
        "monster_id": str(row["monster_id"]),
        "monster_name": str(row["monster_name"]),
        "location": str(row["location"]),
        "difficulty": str(row["difficulty"]),
        "synonyms": str(row["synonyms"]),
        "description": str(row["description"])
    }]    
    client.insert(collection_name=collection_name, data=data_to_insert)

# 测试搜索
search_query = "高难度妖怪"
search_embedding = embedding_function([search_query])[0]
search_result = client.search(
    collection_name=collection_name,
    data=[search_embedding.tolist()],
    limit=3,
    output_fields=["monster_name", "location", "difficulty", "synonyms"]
)
print(f"搜索结果 '{search_query}':", search_result)

# 测试条件查询
query_result = client.query(
    collection_name=collection_name,
    filter="difficulty == 'Low'",
    output_fields=["monster_name", "location", "difficulty", "synonyms"]
)
print(f"难度为Low的妖怪：", query_result)
