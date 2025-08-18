from pymilvus import MilvusClient, DataType
import random

# 1. 设置 Milvus 客户端
client = MilvusClient(uri="http://localhost:19530")
COLLECTION_NAME = "flat_index_demo"

# 如果集合已存在，则删除
if client.has_collection(COLLECTION_NAME):
    client.drop_collection(COLLECTION_NAME)

# 2. 创建 schema
schema = MilvusClient.create_schema(auto_id=False, enable_dynamic_field=True)
schema.add_field(field_name="id", datatype=DataType.INT64, is_primary=True)
schema.add_field(field_name="vector", datatype=DataType.FLOAT_VECTOR, dim=128)

# 3. 创建集合
client.create_collection(collection_name=COLLECTION_NAME, schema=schema)

# 4. 插入随机向量数据
num_vectors = 1000
vectors = [[random.random() for _ in range(128)] for _ in range(num_vectors)]
ids = list(range(num_vectors))
entities = [{"id": ids[i], "vector": vectors[i]} for i in range(num_vectors)]

client.insert(collection_name=COLLECTION_NAME, data=entities)
# flush 保证数据落盘
# client.flush([COLLECTION_NAME])

# 5. 创建索引（此时集合中已有数据）
index_params = MilvusClient.prepare_index_params()
index_params.add_index(
    field_name="vector",
    metric_type="L2",
    index_type="IVF_FLAT",
    index_name="vector_index",
    params={
        "nlist": 64  # 设置聚类数量
    }
)
client.create_index(
    collection_name=COLLECTION_NAME,
    index_params=index_params,
    sync=True
)

# 验证索引
print("索引列表:", client.list_indexes(collection_name=COLLECTION_NAME))
print("索引详情:", client.describe_index(
    collection_name=COLLECTION_NAME,
    index_name="vector_index"
))

# 6. load 后再搜索
client.load_collection(collection_name=COLLECTION_NAME)
search_vectors = [[random.random() for _ in range(128)]]
results = client.search(
    collection_name=COLLECTION_NAME,
    data=search_vectors,
    ann_field="vector",
    limit=5,
    output_fields=["id"],
    search_params={
        "params": {
            "nprobe": 10  # 设置搜索时检查的聚类数量
        }
    }
)

print("\n搜索结果:")
for hits in results:
    for hit in hits:
        # 注意用 dict 方式访问
        print(f"ID: {hit['id']}, 距离: {hit['distance']}")

# 清理
client.release_collection(collection_name=COLLECTION_NAME)
# client.disconnect()
