from pymilvus import MilvusClient, DataType
import random
import numpy as np

'''
L2 适合连续数据
IP 适合非标准化数据
COSINE 适合方向相似性比较
'''

# 1. 设置 Milvus 客户端
client = MilvusClient(uri="http://localhost:19530")

# 定义指标类型和对应的集合名称
metric_types = ["L2", "IP", "COSINE"]
collections = {metric: f"ann_search_demo_{metric.lower()}" for metric in metric_types}

# 2. 创建数据
def create_data(num_vectors=1000, dim=128):
    vectors = [[random.random() for _ in range(dim)] for _ in range(num_vectors)]
    ids = list(range(num_vectors))
    colors = [f"color_{random.randint(1, 1000)}" for _ in range(num_vectors)]
    return vectors, ids, colors

vectors, ids, colors = create_data()

# 3. 为每种指标类型创建集合和索引
def create_collection_with_metric(collection_name, metric_type):
    # 如果集合已存在，则删除
    if client.has_collection(collection_name):
        client.drop_collection(collection_name)

    # 创建 schema
    schema = MilvusClient.create_schema(auto_id=False, enable_dynamic_field=True)
    schema.add_field(field_name="id", datatype=DataType.INT64, is_primary=True)
    schema.add_field(field_name="vector", datatype=DataType.FLOAT_VECTOR, dim=128)
    schema.add_field(field_name="color", datatype=DataType.VARCHAR, max_length=100)

    # 创建集合
    client.create_collection(collection_name=collection_name, schema=schema)

    # 插入数据
    entities = [{"id": ids[i], "vector": vectors[i], "color": colors[i]} for i in range(len(ids))]
    client.insert(collection_name=collection_name, data=entities)

    # 创建索引
    index_params = MilvusClient.prepare_index_params()
    index_params.add_index(
        field_name="vector",
        metric_type=metric_type,
        index_type="FLAT",
        index_name="vector_index",
        params={}
    )
    client.create_index(
        collection_name=collection_name,
        index_params=index_params,
        sync=True
    )

    # 加载集合
    client.load_collection(collection_name=collection_name)

# 为每种指标类型创建集合
for metric_type, collection_name in collections.items():
    print(f"\n创建 {metric_type} 指标类型的集合...")
    create_collection_with_metric(collection_name, metric_type)

# 4. 生成查询向量
def normalize_vector(vector):
    norm = np.linalg.norm(vector)
    if norm == 0:
        return vector
    return vector / norm

query_vector = [random.random() for _ in range(128)]
normalized_query_vector = normalize_vector(query_vector)

# 5. 使用不同指标类型进行搜索
print("\n=== 不同指标类型的搜索比较 ===")
for metric_type, collection_name in collections.items():
    print(f"\n使用 {metric_type} 指标类型搜索:")
    search_vector = normalized_query_vector if metric_type == "COSINE" else query_vector
    
    results = client.search(
        collection_name=collection_name,
        data=[search_vector],
        anns_field="vector",
        limit=3,
        search_params={"metric_type": metric_type},
        output_fields=["color"]
    )
    
    print(f"搜索结果 (指标类型: {metric_type}):")
    for hits in results:
        for hit in hits:
            print(f"ID: {hit['id']}, 距离: {hit['distance']}, 颜色: {hit['entity']['color']}")

# 6. 批量向量搜索示例
print("\n=== 批量向量搜索（不同指标类型）===")
query_vectors = [[random.random() for _ in range(128)] for _ in range(2)]
normalized_query_vectors = [normalize_vector(v) for v in query_vectors]

for metric_type, collection_name in collections.items():
    print(f"\n使用 {metric_type} 指标类型进行批量搜索:")
    search_vectors = normalized_query_vectors if metric_type == "COSINE" else query_vectors
    
    results = client.search(
        collection_name=collection_name,
        data=search_vectors,
        anns_field="vector",
        limit=3,
        search_params={"metric_type": metric_type}
    )
    
    print(f"批量搜索结果 (指标类型: {metric_type}):")
    for i, hits in enumerate(results):
        print(f"\n查询向量 {i+1} 的结果:")
        for hit in hits:
            print(f"ID: {hit['id']}, 距离: {hit['distance']}")

# 7. 清理
for collection_name in collections.values():
    client.release_collection(collection_name=collection_name)
