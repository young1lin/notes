from pymilvus import MilvusClient, DataType
import random

# 1. 设置 Milvus 客户端
client = MilvusClient(uri="http://localhost:19530")
COLLECTION_NAME = "search_iterator_demo"

# 如果集合已存在，则删除
if client.has_collection(COLLECTION_NAME):
    client.drop_collection(COLLECTION_NAME)

# 2. 创建 schema
schema = MilvusClient.create_schema(auto_id=False, enable_dynamic_field=True)
schema.add_field(field_name="id", datatype=DataType.INT64, is_primary=True)
schema.add_field(field_name="vector", datatype=DataType.FLOAT_VECTOR, dim=128)
schema.add_field(field_name="color", datatype=DataType.VARCHAR, max_length=100)

# 3. 创建集合
client.create_collection(collection_name=COLLECTION_NAME, schema=schema)

# 4. 插入随机向量数据
num_vectors = 20000  # 插入更多数据以演示 SearchIterator
vectors = [[random.random() for _ in range(128)] for _ in range(num_vectors)]
ids = list(range(num_vectors))
colors = [f"color_{random.randint(1, 1000)}" for _ in range(num_vectors)]
entities = [{"id": ids[i], "vector": vectors[i], "color": colors[i]} for i in range(num_vectors)]

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

# 7. 使用 SearchIterator 进行搜索
print("\n=== 使用 SearchIterator 进行搜索 ===")
query_vector = [random.random() for _ in range(128)]

# 创建 SearchIterator
iterator = client.search_iterator(
    collection_name=COLLECTION_NAME,
    data=[query_vector],
    anns_field="vector",
    search_params={"metric_type": "L2"},
    batch_size=1000,  # 每批返回1000条结果
    limit=20000,      # 总共返回20000条结果
    output_fields=["color"]
)

# 使用迭代器获取结果
all_results = []
while True:
    result = iterator.next()
    if not result:
        iterator.close()
        break
    
    # 将结果转换为字典并添加到结果列表
    for hit in result:
        all_results.append(hit.to_dict())

print(f"总共获取到 {len(all_results)} 条结果")
print("\n前5条结果:")
for result in all_results[:5]:
    print(f"ID: {result['id']}, 距离: {result['distance']}, 颜色: {result['entity']['color']}")

# 8. 清理
client.release_collection(collection_name=COLLECTION_NAME)
