# 1. 加载并预处理数据集
import json
from typing import Optional, Dict
with open("90-文档-Data/灭神纪/战斗场景.json", 'r', encoding='utf-8') as f:
    dataset = json.load(f)

docs = []
metadata = []

for item in dataset['data']:
    text_parts = [item['title'], item['description']]

    if 'combat_details' in item:
        text_parts.extend(item['combat_details'].get('combat_style', []))
        text_parts.extend(item['combat_details'].get('abilities_used', []))

    if 'scene_info' in item:
        text_parts.extend([
            item['scene_info'].get('location', ''),
            item['scene_info'].get('environment', ''),
            item['scene_info'].get('time_of_day', '')
        ])

    docs.append(' '.join(filter(None, text_parts)))
    metadata.append(item)

print(f"加载了 {len(docs)} 条数据")

# 2. 导入并使用 BGE-M3 生成嵌入向量
from milvus_model.hybrid import BGEM3EmbeddingFunction

ef = BGEM3EmbeddingFunction(use_fp16=False, device="cpu")
print("开始生成向量嵌入……")
docs_embeddings = ef(docs)
print(f"向量生成完成，密集向量维度：{ef.dim['dense']}")

# 3. 导入 Milvus 并连接服务
from pymilvus import (
    connections,
    utility,
    FieldSchema,
    CollectionSchema,
    DataType,
    Collection
)

collection_name = "wukong_hybrid"
connections.connect(uri="./wukong.db")

# 4. 创建 Milvus 集合和索引
fields = [
    FieldSchema(name="pk", dtype=DataType.VARCHAR, is_primary=True, auto_id=True, max_length=100),
    FieldSchema(name="text", dtype=DataType.VARCHAR, max_length=2048),
    FieldSchema(name="id", dtype=DataType.VARCHAR, max_length=100),
    FieldSchema(name="title", dtype=DataType.VARCHAR, max_length=256),
    FieldSchema(name="category", dtype=DataType.VARCHAR, max_length=64),
    FieldSchema(name="location", dtype=DataType.VARCHAR, max_length=128),
    FieldSchema(name="environment", dtype=DataType.VARCHAR, max_length=64),
    FieldSchema(name="sparse_vector", dtype=DataType.SPARSE_FLOAT_VECTOR),
    FieldSchema(name="dense_vector", dtype=DataType.FLOAT_VECTOR, dim=ef.dim["dense"])
]

schema = CollectionSchema(fields)

if utility.has_collection(collection_name):
    utility.drop_collection(collection_name)

collection = Collection(name=collection_name, schema=schema, consistency_level="Strong")

collection.create_index("sparse_vector", {"index_type": "SPARSE_INVERTED_INDEX", "metric_type": "IP"})
collection.create_index("dense_vector", {"index_type": "AUTOINDEX", "metric_type": "IP"})

collection.load()

# 5. 插入数据到集合中
batch_size = 50
for i in range(0, len(docs), batch_size):
    end_idx = min(i + batch_size, len(docs))
    batch_data = []

    for j in range(i, end_idx):
        item = metadata[j]
        batch_data.append({
            "text": docs[j],
            "id": item["id"],
            "title": item["title"],
            "category": item["category"],
            "location": item.get("scene_info", {}).get("location", ""),
            "environment": item.get("scene_info", {}).get("environment", ""),
            "sparse_vector": docs_embeddings["sparse"]._getrow(j),
            "dense_vector": docs_embeddings["dense"][j]
        })

    collection.insert(batch_data)

print(f"数据插入完成，总数：{collection.num_entities}")

# 6. 定义并执行混合搜索
from pymilvus import AnnSearchRequest, WeightedRanker, RRFRanker

query = "雪地中的战斗场景"
category = "combat"
environment = "雪地"
limit = 5
search_type = "hybrid"
rerank_method = "rrf"  # 可选：'weighted' 或 'rrf'
weights = {"sparse": 0.7, "dense": 1.0}
rrf_k = 60  # RRF 参数

query_embeddings = ef([query])

# 打印查询向量的详细信息
print("\n查询向量信息：")
print(f"密集向量维度：{len(query_embeddings['dense'][0])}")
sparse_row = query_embeddings['sparse']._getrow(0)
print(f"稀疏向量非零元素数量：{sparse_row.nnz}")
print(f"稀疏向量非零元素示例：{dict(zip(sparse_row.indices[:5], sparse_row.data[:5]))}")

# 构建过滤表达式
expr = None
conditions = []
if category:
    conditions.append(f'category == "{category}"')
if environment:
    conditions.append(f'environment == "{environment}"')
if conditions:
    expr = " && ".join(conditions)

search_params = {
    "metric_type": "IP",
    "params": {}
}
if expr:
    search_params["expr"] = expr

if search_type == "hybrid":
    dense_req = AnnSearchRequest(
        data=[query_embeddings["dense"][0]],
        anns_field="dense_vector",
        param=search_params,
        limit=limit
    )
    sparse_req = AnnSearchRequest(
        data=[query_embeddings["sparse"]._getrow(0)],
        anns_field="sparse_vector",
        param=search_params,
        limit=limit
    )
    
    # 根据选择的重排方法创建不同的重排器
    if rerank_method == "weighted":
        rerank = WeightedRanker(weights["sparse"], weights["dense"])
        print(f"\n使用加权重排，权重：稀疏={weights['sparse']}, 密集={weights['dense']}")
    else:  # rrf
        rerank = RRFRanker(rrf_k)
        print(f"\n使用 RRF 重排，k={rrf_k}")
    
    # 执行混合搜索前，先分别执行稀疏和密集搜索
    print("\n执行单独搜索：")
    dense_results = collection.search(
        data=[query_embeddings["dense"][0]],
        anns_field="dense_vector",
        param=search_params,
        limit=limit,
        output_fields=["text", "id", "title", "category", "location", "environment"]
    )[0]
    
    sparse_results = collection.search(
        data=[query_embeddings["sparse"]._getrow(0)],
        anns_field="sparse_vector",
        param=search_params,
        limit=limit,
        output_fields=["text", "id", "title", "category", "location", "environment"]
    )[0]
    
    print("\n密集向量搜索结果：")
    for i, hit in enumerate(dense_results):
        print(f"{i+1}. {hit.entity.title} (分数: {hit.distance:.4f})")
    
    print("\n稀疏向量搜索结果：")
    for i, hit in enumerate(sparse_results):
        print(f"{i+1}. {hit.entity.title} (分数: {hit.distance:.4f})")
    
    # 执行混合搜索
    results = collection.hybrid_search(
        reqs=[dense_req, sparse_req],
        rerank=rerank,
        limit=limit,
        output_fields=["text", "id", "title", "category", "location", "environment"]
    )[0]
else:
    field = "dense_vector" if search_type == "dense" else "sparse_vector"
    vec = query_embeddings["dense"][0] if search_type == "dense" else query_embeddings["sparse"]._getrow(0)
    results = collection.search(
        data=[vec],
        anns_field=field,
        param=search_params,
        limit=limit,
        output_fields=["text", "id", "title", "category", "location", "environment"]
    )[0]

# 7. 展示搜索结果
print(f"\n查询：{query}")
print("\n混合搜索结果：")
for i, hit in enumerate(results):
    print(f"\n{i+1}. {hit.entity.title}")
    print(f"ID：{hit.entity.id}")
    print(f"类别：{hit.entity.category}")
    print(f"位置：{hit.entity.location}")
    print(f"环境：{hit.entity.environment}")
    print(f"最终相似度分数：{hit.distance:.4f}")
    print(f"文本：{hit.entity.text[:200]}...")