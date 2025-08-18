from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from llama_index.core.postprocessor import SentenceEmbeddingOptimizer
# 加载文档
documents = SimpleDirectoryReader("data/山西文旅").load_data()  
index = VectorStoreIndex.from_documents(documents)
# 不使用优化的查询
print("不使用优化：")
query_engine = index.as_query_engine()
response = query_engine.query("山西省的主要旅游景点有哪些？")
print(f"答案：{response}")
# 使用优化（百分比截断）
print("\n使用优化（percentile_cutoff=0.5）：")
query_engine = index.as_query_engine(node_postprocessors=[SentenceEmbeddingOptimizer(percentile_cutoff=0.5)])
response = query_engine.query("山西省的主要旅游景点有哪些？")
print(f"答案：{response}")
# 使用优化（阈值截断）
print("\n使用优化（threshold_cutoff=0.7）：")
query_engine = index.as_query_engine(node_postprocessors=[SentenceEmbeddingOptimizer(threshold_cutoff=0.7)])
response = query_engine.query("山西省的主要旅游景点有哪些？")
print(f"答案：{response}")
