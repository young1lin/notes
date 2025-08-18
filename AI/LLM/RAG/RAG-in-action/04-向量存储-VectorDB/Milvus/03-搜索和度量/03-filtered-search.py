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
schema.add_field(field_name="likes", datatype=DataType.INT64)

# 3. 创建集合
client.create_collection(collection_name=COLLECTION_NAME, schema=schema)

# 4. 插入随机向量数据
num_vectors = 1000
vectors = [[random.random() for _ in range(128)] for _ in range(num_vectors)]
ids = list(range(num_vectors))
colors = [f"color_{random.randint(1, 1000)}" for _ in range(num_vectors)]
likes = [random.randint(1, 1000) for _ in range(num_vectors)]
entities = [{"id": ids[i], "vector": vectors[i], "color": colors[i], "likes": likes[i]} for i in range(num_vectors)]

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

# 7. 标准过滤搜索示例
print("\n=== 标准过滤搜索 ===")
query_vector = [random.random() for _ in range(128)]
results = client.search(
    collection_name=COLLECTION_NAME,
    data=[query_vector],
    anns_field="vector",
    limit=3,
    search_params={"metric_type": "L2"},
    filter='color like "color_%" and likes > 500',  # 过滤条件：颜色以color_开头且点赞数大于500
    output_fields=["color", "likes"]  # 指定输出字段
)

print("过滤搜索结果:")
for hits in results:
    for hit in hits:
        print(f"ID: {hit['id']}, 距离: {hit['distance']}, 颜色: {hit['entity']['color']}, 点赞数: {hit['entity']['likes']}")

# 8. 迭代过滤搜索示例
print("\n=== 迭代过滤搜索 ===")
results = client.search(
    collection_name=COLLECTION_NAME,
    data=[query_vector],
    anns_field="vector",
    limit=3,
    search_params={
        "metric_type": "L2",
        "hints": "iterative_filter"  # 启用迭代过滤
    },
    filter='color like "color_%" and likes > 500',
    output_fields=["color", "likes"]
)

print("迭代过滤搜索结果:")
for hits in results:
    for hit in hits:
        print(f"ID: {hit['id']}, 距离: {hit['distance']}, 颜色: {hit['entity']['color']}, 点赞数: {hit['entity']['likes']}")

# 9. 清理
client.release_collection(collection_name=COLLECTION_NAME)
