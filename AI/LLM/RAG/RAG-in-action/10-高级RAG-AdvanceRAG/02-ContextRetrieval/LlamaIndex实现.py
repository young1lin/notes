# 文件名: contextual_retrieval.py
# 描述: 本文件演示了如何使用LlamaIndex实现上下文检索，并进行了错误处理优化
# 原文档链接: https://docs.llamaindex.ai/en/stable/examples/cookbooks/contextual_retrieval/

import os
import pandas as pd
from llama_index.core import Document, VectorStoreIndex
from llama_index.core.node_parser import SentenceSplitter
from llama_index.core.schema import TextNode
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core.evaluation import generate_question_context_pairs, RetrieverEvaluator
from llama_index.postprocessor.cohere_rerank import CohereRerank
from llama_index.retrievers.bm25 import BM25Retriever
from llama_index.core.retrievers import BaseRetriever
from llama_index.core import QueryBundle
from llama_index.core.schema import NodeWithScore
from typing import List
import asyncio
from dotenv import load_dotenv
load_dotenv()

# --- 安装必要的库 ---
# !pip install llama-index llama-index-llms-openai llama-index-embeddings-openai llama-index-postprocessor-cohere-rerank llama-index-retrievers-bm25 pandas

# --- 设置API密钥 ---
# 请确保环境变量中设置了OPENAI_API_KEY和COHERE_API_KEY
# os.environ["OPENAI_API_KEY"] = "your-openai-api-key"
# os.environ["COHERE_API_KEY"] = "your-cohere-api-key"

# --- 设置LLM和Embedding模型 ---
llm = OpenAI(model="gpt-3.5-turbo")  # 可以替换为gpt-4以获得更好的效果
embed_model = OpenAIEmbedding(model="text-embedding-ada-002")

# --- 示例文本数据 ---
paul_graham_essay_text = """
What I Worked On
February 2021
Before college the two main things I worked on, outside of school, were writing and programming. I wrote short stories and I programmed on an IBM 1401 at my father's company on weekends. I got interested in philosophy in college. I don't know if I would have had I not gone to college, but I suspect not, because the philosophy I encountered in college is quite different from what you'd encounter outside it.
I wanted to study philosophy in college, but my father, who was paying, said I couldn't because it wasn't practical. So I majored in something called "Science and Literature" which was offered by the college of Arts and Sciences but counted as science. I wrote my thesis on the philosophy of mathematics, based on the work of Friedrich Waismann, himself a disciple of Wittgenstein.
I went to grad school in philosophy at Cornell, but after a year I left to study computer science at Harvard, which seemed more exciting. After the first year I started working on what would become Common Lisp. That led to me working with some people starting a company called Lucid. I didn't officially join Lucid, but I spent a lot of time there. In the meantime I also worked on On Lisp. After college I'd occasionally written short essays. In grad school I wrote more of them, and started to publish them on the web, and after that I started to get invited to give talks. In the summer of 1995 I was invited to give a talk at a conference on programming language design. I couldn't take time off because I was running a small consulting business. So I decided to write an essay instead.
"""

# --- 为每个块创建上下文的提示模板 ---
CONTEXT_PROMPT_TEMPLATE = """
以下是文档中的一段文本:
"{context_str}"

请根据这段文本，创建一个简洁的句子来描述这段文本的内容。
这将用于回答有关文档的问题。
上下文: """

# --- 工具函数 ---

# 创建基于Embedding的检索器
def create_embedding_retriever(nodes, similarity_top_k=3):
    """创建基于向量嵌入的检索器"""
    # 确保similarity_top_k不超过节点数
    adjusted_top_k = min(similarity_top_k, len(nodes))
    if adjusted_top_k < similarity_top_k:
        print(f"警告：由于节点数量限制，将similarity_top_k从{similarity_top_k}调整为{adjusted_top_k}")
    
    # 确保至少为1
    adjusted_top_k = max(1, adjusted_top_k)
    
    index = VectorStoreIndex(nodes, embed_model=embed_model)
    return index.as_retriever(similarity_top_k=adjusted_top_k)

# 创建基于BM25的检索器
def create_bm25_retriever(nodes, similarity_top_k=3):
    """创建基于BM25算法的检索器"""
    # 将节点转换为TextNode
    text_nodes = [TextNode(text=node.get_content(), id_=node.node_id) for node in nodes if hasattr(node, 'get_content')]
    
    # 检查是否有有效节点
    if not text_nodes:
        print("警告：没有有效的TextNode用于BM25检索器")
        text_nodes = [TextNode(text="样本文本", id_="sample_id")]
    
    # 调整top_k，确保不超过语料库大小
    adjusted_top_k = min(similarity_top_k, len(text_nodes))
    if adjusted_top_k < similarity_top_k:
        print(f"警告：由于语料库大小限制，将similarity_top_k从{similarity_top_k}调整为{adjusted_top_k}")
    
    # 确保至少为1
    adjusted_top_k = max(1, adjusted_top_k)
    
    return BM25Retriever.from_defaults(nodes=text_nodes, similarity_top_k=adjusted_top_k)

# 混合检索器（Embedding + BM25 + Reranker）
class EmbeddingBM25RerankerRetriever(BaseRetriever):
    """将向量检索、BM25检索和重排序器结合的混合检索器"""
    def __init__(self, embedding_retriever, bm25_retriever, reranker):
        self._embedding_retriever = embedding_retriever
        self._bm25_retriever = bm25_retriever
        self._reranker = reranker
        super().__init__()

    def _retrieve(self, query_bundle: QueryBundle) -> List[NodeWithScore]:
        # 获取两种检索器的结果
        embedding_nodes = self._embedding_retriever.retrieve(query_bundle)
        bm25_nodes = self._bm25_retriever.retrieve(query_bundle)

        # 合并并去重
        all_nodes = {node.node.node_id: node for node in embedding_nodes}
        for node in bm25_nodes:
            if node.node.node_id not in all_nodes:
                all_nodes[node.node.node_id] = node
        
        # 准备用于重排序的节点
        nodes_for_rerank = list(all_nodes.values())
        
        # 使用重排序器处理节点
        if self._reranker and nodes_for_rerank:
            reranked_nodes = self._reranker.postprocess_nodes(
                nodes_for_rerank, query_bundle=query_bundle
            )
            return reranked_nodes
        return nodes_for_rerank

# 评估函数
async def retrieval_results(retriever, qa_dataset):
    """评估检索器的性能"""
    try:
        retriever_evaluator = RetrieverEvaluator.from_metric_names(
            ["mrr", "hit_rate"], retriever=retriever
        )
        eval_results = await retriever_evaluator.aevaluate_dataset(qa_dataset)
        return eval_results
    except Exception as e:
        print(f"评估过程中出错: {str(e)}")
        # 返回空结果避免中断执行
        return []

# 显示结果
def display_results(name, eval_results):
    """显示评估结果"""
    if not eval_results:
        # 处理空结果情况
        return pd.DataFrame({
            "retrievers": [name], 
            "hit_rate": [0.0], 
            "mrr": [0.0], 
            "note": ["评估失败"]
        })
    
    metric_dicts = []
    for eval_result in eval_results:
        metric_dict = eval_result.metric_vals_dict
        metric_dicts.append(metric_dict)
    
    if not metric_dicts:
        return pd.DataFrame({
            "retrievers": [name], 
            "hit_rate": [0.0], 
            "mrr": [0.0], 
            "note": ["无评估数据"]
        })
    
    full_df = pd.DataFrame(metric_dicts)
    
    hit_rate = full_df["hit_rate"].mean() if "hit_rate" in full_df else 0.0
    mrr = full_df["mrr"].mean() if "mrr" in full_df else 0.0
    
    # 计算其他指标(如果存在)
    metrics = {"retrievers": [name], "hit_rate": [hit_rate], "mrr": [mrr]}
    for metric in ["precision", "recall", "ap", "ndcg"]:
        if metric in full_df:
            metrics[metric] = [full_df[metric].mean()]
    
    return pd.DataFrame(metrics)

# --- 主函数 ---
async def main():
    print("开始上下文检索实验")
    
    # 1. 将文档转换为Document对象
    documents = [Document(text=paul_graham_essay_text)]
    print("文档加载完成")
    
    # 2. 分割文档为节点（块）
    # 使用更大的块大小，确保生成足够的节点用于实验
    splitter = SentenceSplitter(chunk_size=256, chunk_overlap=50)
    nodes = splitter.get_nodes_from_documents(documents)
    print(f"创建了 {len(nodes)} 个节点")
    
    # 调试：显示节点数量，如果太少可能会导致BM25检索器问题
    if len(nodes) < 3:
        print("警告：节点数量少于3，可能导致检索器评估问题")
        # 如果节点太少，我们创建额外的样本节点
        while len(nodes) < 3:
            sample_text = f"样本文本 {len(nodes)+1}：这是一个用于测试的额外文本节点。"
            sample_node = TextNode(text=sample_text, id_=f"sample_node_{len(nodes)}")
            nodes.append(sample_node)
        print(f"已添加样本节点，现在共有 {len(nodes)} 个节点")
    
    # 3. 为每个节点生成上下文描述
    nodes_contextual = []
    for node in nodes:
        # 模拟LLM生成的上下文
        simulated_context = f"本段内容讨论了: {node.get_content()[:50]}..."
        new_metadata = node.metadata.copy() if hasattr(node, 'metadata') else {}
        new_metadata["generated_context"] = simulated_context
        
        # 创建带有生成上下文的新节点
        contextual_node = TextNode(
            text=node.get_content(),
            metadata=new_metadata,
            id_=node.node_id
        )
        nodes_contextual.append(contextual_node)
    
    # 4. 设置检索参数 - 确保不超过节点数量
    similarity_top_k = min(3, len(nodes))
    print(f"检索参数 similarity_top_k 设置为 {similarity_top_k}")
    
    # 5. 设置Cohere重排序器
    try:
        cohere_rerank = CohereRerank(
            api_key=os.environ.get("COHERE_API_KEY", "your-api-key"), 
            model="rerank-english-v3.0",
            top_n=similarity_top_k
        )
        print("Cohere重排序器设置完成")
    except Exception as e:
        print(f"设置Cohere重排序器时出错: {str(e)}")
        cohere_rerank = None
    
    # 6. 创建各种检索器
    print("创建标准检索器...")
    embedding_retriever = create_embedding_retriever(
        nodes, similarity_top_k=similarity_top_k
    )
    bm25_retriever = create_bm25_retriever(
        nodes, similarity_top_k=similarity_top_k
    )
    embedding_bm25_retriever_rerank = EmbeddingBM25RerankerRetriever(
        embedding_retriever, bm25_retriever, reranker=cohere_rerank
    )
    
    print("创建上下文检索器...")
    contextual_embedding_retriever = create_embedding_retriever(
        nodes_contextual, similarity_top_k=similarity_top_k
    )
    contextual_bm25_retriever = create_bm25_retriever(
        nodes_contextual, similarity_top_k=similarity_top_k
    )
    contextual_embedding_bm25_retriever_rerank = EmbeddingBM25RerankerRetriever(
        contextual_embedding_retriever,
        contextual_bm25_retriever,
        reranker=cohere_rerank,
    )
    
    # 7. 创建评估数据集
    print("创建评估数据集...")
    from llama_index.core.evaluation import EmbeddingQAFinetuneDataset
    
    fixed_queries = {
        "q1": "作者在大学前做了什么工作？",
        "q2": "作者在大学里选择了什么专业？",
        "q3": "作者在毕业后做了什么工作？"
    }
    
    # 设置相关文档映射
    relevant_docs_mapping = {}
    if nodes:
        for i, query_id in enumerate(fixed_queries.keys()):
            node_index = min(i, len(nodes)-1)  # 确保索引不超出范围
            relevant_docs_mapping[query_id] = [nodes[node_index].node_id]
    
    # 构建语料库
    corpus_data = {}
    if nodes:
        corpus_data = {node.node_id: node.get_content() for node in nodes}
    
    # 创建评估数据集
    qa_dataset = EmbeddingQAFinetuneDataset(
        queries=fixed_queries,
        corpus=corpus_data,
        relevant_docs=relevant_docs_mapping
    )
    
    # 8. 评估检索器性能
    print("\n--- 评估标准检索器 ---")
    embedding_retriever_results = await retrieval_results(
        embedding_retriever, qa_dataset
    )
    bm25_retriever_results = await retrieval_results(
        bm25_retriever, qa_dataset
    )
    embedding_bm25_retriever_rerank_results = await retrieval_results(
        embedding_bm25_retriever_rerank, qa_dataset
    )
    
    # 9. 评估上下文检索器
    print("\n--- 评估上下文检索器 ---")
    contextual_embedding_retriever_results = await retrieval_results(
        contextual_embedding_retriever, qa_dataset
    )
    contextual_bm25_retriever_results = await retrieval_results(
        contextual_bm25_retriever, qa_dataset
    )
    contextual_embedding_bm25_retriever_rerank_results = await retrieval_results(
        contextual_embedding_bm25_retriever_rerank, qa_dataset
    )
    
    # 10. 显示结果
    print("\n--- 不带上下文的检索结果 ---")
    standard_results = pd.concat(
        [
            display_results("Embedding检索器", embedding_retriever_results),
            display_results("BM25检索器", bm25_retriever_results),
            display_results(
                "Embedding + BM25 + 重排序检索器",
                embedding_bm25_retriever_rerank_results,
            ),
        ],
        ignore_index=True,
        axis=0,
    )
    print(standard_results)
    
    print("\n--- 带上下文的检索结果 ---")
    contextual_results = pd.concat(
        [
            display_results(
                "上下文Embedding检索器",
                contextual_embedding_retriever_results,
            ),
            display_results(
                "上下文BM25检索器", 
                contextual_bm25_retriever_results
            ),
            display_results(
                "上下文Embedding + 上下文BM25 + 重排序检索器",
                contextual_embedding_bm25_retriever_rerank_results,
            ),
        ],
        ignore_index=True,
        axis=0,
    )
    print(contextual_results)
    
if __name__ == "__main__":
    asyncio.run(main())

