from llama_index.core import SimpleDirectoryReader
from llama_index.core.node_parser import (
    SentenceSplitter,
    SemanticSplitterNodeParser,
)
from llama_index.embeddings.openai import OpenAIEmbedding 
# from llama_index.embeddings.huggingface import HuggingFaceEmbedding 
# embed_model = HuggingFaceEmbedding(model_name="BAAI/bge-small-zh")
documents = SimpleDirectoryReader(input_files=["90-文档-Data/黑悟空/黑悟空wiki.txt"]).load_data()

# 创建语义分块器
splitter = SemanticSplitterNodeParser(
    buffer_size=3,  # 缓冲区大小
    breakpoint_percentile_threshold=90, # 断点百分位阈值
    embed_model=OpenAIEmbedding()     # 使用的嵌入模型
)
# 创建基础句子分块器（作为对照）
base_splitter = SentenceSplitter(
    # chunk_size=512
)


'''
buffer_size：
默认值为1
这个参数控制评估语义相似度时，将多少个句子组合在一起当设置为1时，每个句子会被单独考虑
当设置大于1时，会将多个句子组合在一起进行评估例如，如果设置为3，就会将每3个句子作为一个组来评估语义相似度

breakpoint_percentile_threshold：
默认值为95
这个参数控制何时在句子组之间创建分割点,它表示余弦不相似度的百分位数阈值,当句子组之间的不相似度超过这个阈值时，就会创建一个新的节点
数值越小，生成的节点就越多（因为更容易达到分割阈值）
数值越大，生成的节点就越少（因为需要更大的不相似度才会分割）

这两个参数共同影响文本的分割效果：
buffer_size 决定了评估语义相似度的粒度
breakpoint_percentile_threshold 决定了分割的严格程度
例如：
如果 buffer_size=2 且 breakpoint_percentile_threshold=90：每2个句子会被组合在一起,当组合之间的不相似度超过90%时就会分割,这会产生相对较多的节点
如果 buffer_size=3 且 breakpoint_percentile_threshold=98：每3个句子会被组合在一起,需要更大的不相似度才会分割,这会产生相对较少的节点
'''


# 使用语义分块器对文档进行分块
semantic_nodes = splitter.get_nodes_from_documents(documents)
print("\n=== 语义分块结果 ===")
print(f"语义分块器生成的块数：{len(semantic_nodes)}")
for i, node in enumerate(semantic_nodes, 1):
    print(f"\n--- 第 {i} 个语义块 ---")
    print(f"内容:\n{node.text}")
    print("-" * 50)

# 使用基础句子分块器对文档进行分块
base_nodes = base_splitter.get_nodes_from_documents(documents)
print("\n=== 基础句子分块结果 ===")
print(f"基础句子分块器生成的块数：{len(base_nodes)}")
for i, node in enumerate(base_nodes, 1):
    print(f"\n--- 第 {i} 个句子块 ---")
    print(f"内容:\n{node.text}")
    print("-" * 50)
