from deepeval.metrics import ContextualPrecisionMetric, AnswerRelevancyMetric
from deepeval.test_case import LLMTestCase

# 定义测试案例
test_case = LLMTestCase(
    input="如果这双鞋不合脚怎么办？",
    actual_output="我们提供30天无理由全额退款服务。",
    expected_output="顾客可以在30天内退货并获得全额退款。",
    retrieval_context=["所有顾客都有资格享受30天无理由全额退款服务。"]
)

# 定义评估指标
contextual_precision = ContextualPrecisionMetric()
answer_relevancy = AnswerRelevancyMetric()

# 运行评估
contextual_precision.measure(test_case)
answer_relevancy.measure(test_case)

print("上下文精确度得分: ", contextual_precision.score)
print("答案相关性得分: ", answer_relevancy.score)
