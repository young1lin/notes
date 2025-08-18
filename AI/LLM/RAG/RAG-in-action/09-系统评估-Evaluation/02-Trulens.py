import os
import chromadb
from chromadb.utils.embedding_functions import OpenAIEmbeddingFunction
from openai import OpenAI as OpenAIClient  # 避免与TruLens的OpenAI类名冲突

# Trulens是一个用于深度学习模型（尤其是LLM应用）可观察性和评估的开源库。
# 它可以帮助开发者跟踪、调试和评估RAG（检索增强生成）等复杂应用的性能。
# - TruSession: 管理评估会话和结果存储。
# - Feedback: 定义评估指标，如相关性、真实性等。
# - TruApp: 将你的应用包装起来，使其可被Trulens监控。
# - instrument: 一个装饰器，用于标记需要跟踪的具体函数。
from trulens.core import TruSession, Feedback, Select
from trulens.apps.app import TruApp, instrument
from trulens.providers.openai import OpenAI as TruLensOpenAI
import numpy as np

# 设置API密钥
# os.environ["OPENAI_API_KEY"] = "your_key_here"

# 初始化嵌入函数
embedding_function = OpenAIEmbeddingFunction(api_key=os.environ.get("OPENAI_API_KEY"),
                                             model_name="text-embedding-ada-002")
chroma_client = chromadb.Client()
vector_store = chroma_client.get_or_create_collection("Info", embedding_function=embedding_function)

# 添加示例数据
vector_store.add("starbucks_info", documents=[
    """
    Starbucks Corporation is an American multinational chain of coffeehouses headquartered in Seattle, Washington.
    As the world's largest coffeehouse chain, Starbucks is seen to be the main representation of the United States' second wave of coffee culture.
    """
])

class RAG:
    # @instrument 是Trulens提供的装饰器，用于"检测"或"装备"一个函数。
    # 被标记后，每当这个函数被调用时，Trulens会记录其输入、输出、执行时间、错误等信息。
    # 这对于理解RAG流程中每个步骤（检索、生成）的具体行为至关重要。
    @instrument
    def retrieve(self, query: str):
        """检索相关文档"""
        results = vector_store.query(query_texts=[query], n_results=2)
        return results["documents"][0] if results["documents"] else []

    @instrument
    def generate_completion(self, query: str, context: list):
        """生成回答"""
        oai_client = OpenAIClient(api_key=os.environ.get("OPENAI_API_KEY"))
        context_str = "\n".join(context) if context else "No context available."
        completion = oai_client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[{"role": "user", "content": f"Context: {context_str}\nQuestion: {query}"}]
        ).choices[0].message.content
        return completion

    @instrument
    def query(self, query: str):
        """完整的RAG查询流程"""
        context = self.retrieve(query)
        return self.generate_completion(query, context)

# 初始化TruLens会话
# TruSession是与Trulens后端（可以是本地SQLite文件或数据库）交互的入口点。
# 它负责管理和存储所有的跟踪数据和评估结果。
# database_redact_keys=True 选项会自动隐藏记录中的敏感信息（如API密钥），确保安全。
session = TruSession(database_redact_keys=True)
# 重置数据库会清空所有之前的记录，确保我们从一个干净的环境开始本次评估。
session.reset_database()

# 初始化TruLens的OpenAI提供者
# Provider是Trulens用来执行评估的"裁判"。这里我们使用OpenAI的gpt-4模型。
# 这些评估本身就是通过向LLM提出问题来完成的，例如："给定的回答是否与上下文一致？"
provider = TruLensOpenAI(model_engine="gpt-4")

# 定义评估指标 (Feedback Functions)
# Feedback是Trulens的核心概念，用于定义我们关心的评估维度。
# 每个Feedback函数都由一个评估器（provider的方法）和选择器（selector）组成。
# 选择器（.on(...)）精确地指定了要评估的应用的哪个部分（输入、输出或中间结果）。

# 1. Groundedness (内容真实性/依据性)
#    - 评估器: provider.groundedness_measure_with_cot_reasons，使用CoT（思维链）来判断回答是否完全基于所提供的上下文。
#    - 选择器: .on(Select.RecordCalls.retrieve.rets) 指定评估的上下文是 `retrieve` 方法的返回结果。
#              .on_output() 指定要评估的内容是RAG应用的最终输出（生成的答案）。
f_groundedness = Feedback(provider.groundedness_measure_with_cot_reasons, name="Groundedness") \
    .on(Select.RecordCalls.retrieve.rets).on_output()

# 2. Answer Relevance (答案相关性)
#    - 评估器: provider.relevance_with_cot_reasons，判断生成的答案是否与原始问题相关。
#    - 选择器: .on_input() 指定评估的上下文是应用的顶层输入（用户的query）。
#              .on_output() 指定要评估的内容是应用的最终输出。
f_answer_relevance = Feedback(provider.relevance_with_cot_reasons, name="Answer Relevance") \
    .on_input().on_output()

# 3. Context Relevance (上下文相关性)
#    - 评估器: provider.context_relevance_with_cot_reasons，判断检索到的上下文与原始问题的相关性。
#    - 选择器: .on_input() 指定评估的上下文是应用的顶层输入。
#              .on(Select.RecordCalls.retrieve.rets[:]) 指定要评估的内容是 `retrieve` 方法返回的上下文列表中的每个元素。
#    - 聚合器: .aggregate(np.mean) 因为上下文可能包含多个文档，使用聚合函数（这里是求平均值）将所有上下文相关性得分合并为一个总分。
f_context_relevance = Feedback(provider.context_relevance_with_cot_reasons, name="Context Relevance") \
    .on_input().on(Select.RecordCalls.retrieve.rets[:]).aggregate(np.mean)

# 设置TruApp
# TruApp将我们的RAG应用实例与上面定义的Feedback函数列表打包在一起。
# 它创建了一个可被Trulens跟踪和评估的"可观察"应用。
rag = RAG()
tru_rag = TruApp(
    rag,
    app_name="RAG",
    app_version="base",
    feedbacks=[f_groundedness, f_answer_relevance, f_context_relevance]
)

# 执行查询并记录
# 使用 `with tru_rag as recording:` 上下文管理器来运行应用。
# 在这个代码块中，对`rag.query()`的调用及其内部所有被`@instrument`标记的方法都会被Trulens自动记录。
# 记录下来的数据（app-json）包含了完整的调用链、输入输出和中间结果。
with tru_rag as recording:
    response = rag.query("What wave of coffee culture is Starbucks seen to represent in the United States?")
    print(f"Response: {response}")

# 查看评估结果
# get_leaderboard() 方法会从数据库中读取记录，并以表格形式展示所有评估的结果。
# 它会显示每个Feedback的平均得分，便于我们快速了解应用的整体性能。
# 这个看板对于比较不同版本应用（例如，更改提示、模型或检索策略）的性能差异非常有用。
print(session.get_leaderboard())
