from pymilvus import MilvusClient, DataType
import random

# 1. 设置 Milvus 客户端
client = MilvusClient(uri="http://localhost:19530")
COLLECTION_NAME = "ann_search_demo"

# 如果集合已存在，则删除
if client.has_collection(COLLECTION_NAME):
    client.drop_collection(COLLECTION_NAME)

# 2. 创建 schema
schema = MilvusClient.create_schema(auto_id=False, enable_dynamic_field=True)
schema.add_field(field_name="id", datatype=DataType.INT64, is_primary=True)
schema.add_field(field_name="vector", datatype=DataType.FLOAT_VECTOR, dim=128)
schema.add_field(field_name="color", datatype=DataType.VARCHAR, max_length=100)
schema.add_field(
    field_name='text', 
    datatype=DataType.VARCHAR, 
    max_length=1000, 
    enable_analyzer=True,  # 启用文本分析
    enable_match=True      # 启用文本匹配
)

# 3. 创建集合
client.create_collection(collection_name=COLLECTION_NAME, schema=schema)

# 4. 插入随机向量数据
num_vectors = 1000
vectors = [[random.random() for _ in range(128)] for _ in range(num_vectors)]
ids = list(range(num_vectors))
colors = [f"color_{random.randint(1, 1000)}" for _ in range(num_vectors)]
texts = [f"text_{random.randint(1, 1000)}" for _ in range(num_vectors)]  # 添加随机文本
entities = [{"id": ids[i], "vector": vectors[i], "color": colors[i], "text": texts[i]} for i in range(num_vectors)]

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

# 7. 单向量搜索示例
print("\n=== 单向量搜索 ===")
query_vector = [random.random() for _ in range(128)]
results = client.search(
    collection_name=COLLECTION_NAME,
    data=[query_vector],
    anns_field="vector",
    limit=3,
    search_params={"metric_type": "L2"}
)

print("搜索结果:")
for hits in results:
    for hit in hits:
        print(f"ID: {hit['id']}, 距离: {hit['distance']}")

# 8. 批量向量搜索示例
print("\n=== 批量向量搜索 ===")
query_vectors = [[random.random() for _ in range(128)] for _ in range(2)]
results = client.search(
    collection_name=COLLECTION_NAME,
    data=query_vectors,
    anns_field="vector",
    limit=3,
    search_params={"metric_type": "L2"}
)

print("批量搜索结果:")
for i, hits in enumerate(results):
    print(f"\n查询向量 {i+1} 的结果:")
    for hit in hits:
        print(f"ID: {hit['id']}, 距离: {hit['distance']}")

# 9. 带输出字段的搜索示例
print("\n=== 带输出字段的搜索 ===")
results = client.search(
    collection_name=COLLECTION_NAME,
    data=[query_vector],
    anns_field="vector",
    limit=3,
    search_params={"metric_type": "L2"},
    output_fields=["color", "text"]  # 添加text字段到输出
)

print("带输出字段的搜索结果:")
for hits in results:
    for hit in hits:
        print(f"ID: {hit['id']}, 距离: {hit['distance']}, 颜色: {hit['entity']['color']}, 文本: {hit['entity']['text']}")

# 10. 文本匹配搜索示例
print("\n=== 文本匹配搜索 ===")
filter = "TEXT_MATCH(text, 'text_1 text_2')"  # 搜索包含text_1或text_2的文档
results = client.search(
    collection_name=COLLECTION_NAME,
    data=[query_vector],
    anns_field="vector",
    filter=filter,
    limit=3,
    search_params={"metric_type": "L2"},
    output_fields=["text"]
)

print("文本匹配搜索结果:")
for hits in results:
    for hit in hits:
        print(f"ID: {hit['id']}, 距离: {hit['distance']}, 文本: {hit['entity']['text']}")

# 11. 清理
client.release_collection(collection_name=COLLECTION_NAME)
