import os
import asyncio
import random
import nest_asyncio
import numpy as np
import pandas as pd
from collections import defaultdict

# 导入 LlamaIndex 相关模块
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.embeddings.huggingface import HuggingFaceEmbedding
from llama_index.core.node_parser import SentenceWindowNodeParser, SentenceSplitter
from llama_index.core import Settings, SimpleDirectoryReader, VectorStoreIndex
from llama_index.core.postprocessor import MetadataReplacementPostProcessor
from llama_index.core.evaluation import (
    DatasetGenerator, QueryResponseDataset,
    CorrectnessEvaluator, SemanticSimilarityEvaluator, RelevancyEvaluator, FaithfulnessEvaluator, PairwiseComparisonEvaluator,
    BatchEvalRunner
)
from llama_index.core.evaluation.eval_utils import get_responses, get_results_df

# 1. 配置 LLM、Embedding、文本切分器
# --------------------------------------------------
# 设置 LLM（大模型）和 Embedding（向量模型）
llm = OpenAI(model="gpt-3.5-turbo", temperature=0.1)
embed_model = HuggingFaceEmbedding(
    model_name="sentence-transformers/all-mpnet-base-v2", max_length=512
)
text_splitter = SentenceSplitter()
node_parser = SentenceWindowNodeParser.from_defaults(
    window_size=3,
    window_metadata_key="window",
    original_text_metadata_key="original_text",
)
# 全局设置
Settings.llm = llm
Settings.embed_model = embed_model
Settings.text_splitter = text_splitter

# 2. 加载 PDF 文档
# --------------------------------------------------
pdf_path = "/home/huangj2/Documents/rag-in-action/90-文档-Data/复杂PDF/IPCC_AR6_WGII_Chapter03.pdf"
documents = SimpleDirectoryReader(input_files=[pdf_path]).load_data()

# 3. 文本切分为节点
# --------------------------------------------------
# 滑动窗口节点
nodes = node_parser.get_nodes_from_documents(documents)
# 基础句子节点
base_nodes = text_splitter.get_nodes_from_documents(documents)

# 4. 构建向量索引
# --------------------------------------------------
sentence_index = VectorStoreIndex(nodes)
base_index = VectorStoreIndex(base_nodes)

# 5. 检索与问答示例
# --------------------------------------------------
# 滑动窗口检索器
query_engine = sentence_index.as_query_engine(
    # 设置检索返回的顶部结果数量为2
    similarity_top_k=2,
    # 使用元数据替换后处理器，将检索结果的元数据替换为窗口内容
    node_postprocessors=[MetadataReplacementPostProcessor(target_metadata_key="window")],
)
window_response = query_engine.query("What are the concerns surrounding the AMOC?")
print("\n【滑动窗口检索器返回】\n", window_response)
# 获取第一个检索结果的窗口内容
window = window_response.source_nodes[0].node.metadata["window"]
# 获取第一个检索结果的原始句子文本
sentence = window_response.source_nodes[0].node.metadata["original_text"]
print(f"Window: {window}")
print("------------------")
print(f"Original Sentence: {sentence}")

# 基础检索器
base_query_engine = base_index.as_query_engine(similarity_top_k=2)
vector_response = base_query_engine.query("What are the concerns surrounding the AMOC?")
print("\n【基础检索器返回】\n", vector_response)

# 打印所有 source_nodes 的原始句子
print("\n【滑动窗口检索器 source_nodes 原始句子】")
for source_node in window_response.source_nodes:
    print(source_node.node.metadata["original_text"])
    print("--------")

# 检查基础检索器 source_nodes 是否包含 AMOC
print("\n【基础检索器 source_nodes 是否包含 'AMOC'】")
for node in vector_response.source_nodes:
    print("AMOC mentioned?", "AMOC" in node.node.text)
    print("--------")

# 遍历输出所有基础检索器的 node.text
print("\n【基础检索器 source_nodes 的 node.text】")
for i, node in enumerate(vector_response.source_nodes):
    print(f"第 {i+1} 个 node.text:")
    print(node.node.text)
    print("--------")

# 6. 评测数据集准备
# --------------------------------------------------
# 采样部分节点用于生成评测问题
num_nodes_eval = 30
sample_eval_nodes = random.sample(base_nodes[:200], num_nodes_eval)
# 生成评测数据集（如未生成可取消注释）
# dataset_generator = DatasetGenerator(
#     sample_eval_nodes,
#     llm=OpenAI(model="gpt-4"),
#     show_progress=True,
#     num_questions_per_chunk=2,
# )
# eval_dataset = await dataset_generator.agenerate_dataset_from_nodes()
# eval_dataset.save_json("90-文档-Data/复杂PDF/ipcc_eval_qr_dataset.json")

# 加载已生成的评测数据集
eval_dataset = QueryResponseDataset.from_json("90-文档-Data/复杂PDF/ipcc_eval_qr_dataset.json")

# 7. 构建评测器
# --------------------------------------------------
evaluator_c = CorrectnessEvaluator(llm=OpenAI(model="gpt-4"))
evaluator_s = SemanticSimilarityEvaluator()
evaluator_r = RelevancyEvaluator(llm=OpenAI(model="gpt-4"))
evaluator_f = FaithfulnessEvaluator(llm=OpenAI(model="gpt-4"))
# pairwise_evaluator = PairwiseComparisonEvaluator(llm=OpenAI(model="gpt-4"))

evaluator_dict = {
    "correctness": evaluator_c,
    "faithfulness": evaluator_f,
    "relevancy": evaluator_r,
    "semantic_similarity": evaluator_s,
}
batch_runner = BatchEvalRunner(evaluator_dict, workers=2, show_progress=True)

# 8. 评测主流程
# --------------------------------------------------
async def main():
    max_samples = 30
    eval_qs = eval_dataset.questions
    ref_response_strs = [r for (_, r) in eval_dataset.qr_pairs]

    # 重新构建检索器，保证评测一致
    base_query_engine = base_index.as_query_engine(similarity_top_k=2)
    window_query_engine = sentence_index.as_query_engine(
        similarity_top_k=2,
        node_postprocessors=[MetadataReplacementPostProcessor(target_metadata_key="window")],
    )

    # 获取模型回答
    base_pred_responses = get_responses(
        eval_qs[:max_samples], base_query_engine, show_progress=True
    )
    pred_responses = get_responses(
        eval_qs[:max_samples], window_query_engine, show_progress=True
    )

    # 评测
    eval_results = await batch_runner.aevaluate_responses(
        queries=eval_qs[:max_samples],
        responses=pred_responses[:max_samples],
        reference=ref_response_strs[:max_samples],
    )
    base_eval_results = await batch_runner.aevaluate_responses(
        queries=eval_qs[:max_samples],
        responses=base_pred_responses[:max_samples],
        reference=ref_response_strs[:max_samples],
    )
    results_df = get_results_df(
        [eval_results, base_eval_results],
        ["Sentence Window Retriever", "Base Retriever"],
        ["correctness", "relevancy", "faithfulness", "semantic_similarity"],
    )
    print("\n【评测结果】")
    print(results_df)

if __name__ == "__main__":
    nest_asyncio.apply()
    asyncio.run(main())

