from pymilvus import MilvusClient, DataType
import random

# 1. 设置 Milvus 客户端
client = MilvusClient(uri="http://localhost:19530")
COLLECTION_NAME = "group_search_demo"

# 如果集合已存在，则删除
if client.has_collection(COLLECTION_NAME):
    client.drop_collection(COLLECTION_NAME)

# 2. 创建 schema
schema = MilvusClient.create_schema(auto_id=False, enable_dynamic_field=True)
schema.add_field(field_name="id", datatype=DataType.INT64, is_primary=True)
schema.add_field(field_name="vector", datatype=DataType.FLOAT_VECTOR, dim=128)
schema.add_field(field_name="docId", datatype=DataType.INT64)
schema.add_field(field_name="chunk", datatype=DataType.VARCHAR, max_length=100)

# 3. 创建集合
client.create_collection(collection_name=COLLECTION_NAME, schema=schema)

# 4. 插入示例数据
num_vectors = 1000
vectors = [[random.random() for _ in range(128)] for _ in range(num_vectors)]
ids = list(range(num_vectors))
doc_ids = [random.randint(1, 100) for _ in range(num_vectors)]  # 假设有100个文档
chunks = [f"chunk_{random.randint(1, 1000)}" for _ in range(num_vectors)]
entities = [{"id": ids[i], "vector": vectors[i], "docId": doc_ids[i], "chunk": chunks[i]} for i in range(num_vectors)]

client.insert(collection_name=COLLECTION_NAME, data=entities)

# 5. 创建索引
index_params = MilvusClient.prepare_index_params()
index_params.add_index(
    field_name="vector",
    metric_type="L2",
    index_type="FLAT",
    index_name="vector_index",
    params={}
)
client.create_index(
    collection_name=COLLECTION_NAME,
    index_params=index_params,
    sync=True
)

# 6. 加载集合
client.load_collection(collection_name=COLLECTION_NAME)

# 7. 基本分组搜索示例
print("\n=== 基本分组搜索 ===")
query_vector = [random.random() for _ in range(128)]
results = client.search(
    collection_name=COLLECTION_NAME,
    data=[query_vector],
    anns_field="vector",
    limit=5,  # 返回5个不同的文档组
    group_by_field="docId",  # 按文档ID分组
    output_fields=["docId", "chunk"]
)

print("基本分组搜索结果:")
for hits in results:
    for hit in hits:
        print(f"文档ID: {hit['entity']['docId']}, 块: {hit['entity']['chunk']}, 距离: {hit['distance']}")

# 8. 配置组大小的分组搜索示例
print("\n=== 配置组大小的分组搜索 ===")
results = client.search(
    collection_name=COLLECTION_NAME,
    data=[query_vector],
    anns_field="vector",
    limit=3,  # 返回3个不同的文档组
    group_by_field="docId",
    group_size=2,  # 每个组返回2个最相似的结果
    strict_group_size=True,  # 严格确保每个组有2个结果
    output_fields=["docId", "chunk"]
)

print("配置组大小的分组搜索结果:")
for hits in results:
    print(f"\n文档组 {hits[0]['entity']['docId']} 的结果:")
    for hit in hits:
        print(f"块: {hit['entity']['chunk']}, 距离: {hit['distance']}")

# 9. 清理
client.release_collection(collection_name=COLLECTION_NAME)
