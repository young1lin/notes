from langchain.prompts import PromptTemplate
from langchain_community.document_loaders import TextLoader
from langchain.text_splitter import CharacterTextSplitter
from langchain_community.embeddings import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from langchain_openai import OpenAI
import os

# 示例数据
examples = [
    {
        "context": "某大型制造企业的供应链系统出现延迟问题，导致生产效率下降15%。经过调查发现主要是由于供应商管理混乱和库存预测不准确导致。",
        "answer": """问题分析报告：
                    核心问题：供应链效率低下
                    影响程度：生产效率降低15%
                    主要原因：
                    - 供应商管理体系不完善
                    - 库存预测系统准确度不足

                    建议方案：
                    1. 优化供应商评估体系
                    2. 引入智能预测系统
                    3. 建立实时监控机制"""
    },
    {
        "context": "某科技公司的员工流失率达到25%，主要集中在研发部门，影响了产品迭代进度。",
        "answer": """问题分析报告：
                    核心问题：高员工流失率
                    影响程度：流失率25%
                    主要原因：
                    - 薪资福利竞争力不足
                    - 职业发展空间受限

                    建议方案：
                    1. 优化薪酬体系
                    2. 完善晋升机制
                    3. 改善工作环境"""
    }
]

# 创建向量数据库
embeddings = OpenAIEmbeddings(openai_api_key=os.getenv("OPENAI_API_KEY"))
example_texts = [ex["context"] for ex in examples]
db = FAISS.from_texts(example_texts, embeddings)

# 用户输入的问题描述
current_issue = """某零售连锁企业的客户投诉率在过去三个月上升40%，主要集中在配送时效和商品质量两个方面，影响了品牌声誉。"""

# 检索最相似的示例
docs = db.similarity_search(current_issue, k=1)
most_similar_example = next(ex for ex in examples if ex["context"] == docs[0].page_content)

# 构建提示词
prompt = """这是一个企业问题分析示例：

示例：
基于以下情况：
{example_context}

{example_answer}

现在，请基于以下问题，按照相同格式生成分析报告：
{input_context}

请保持分析的专业性和可操作性。
"""

# 创建LLM
llm = OpenAI(openai_api_key=os.getenv("OPENAI_API_KEY"))

# 格式化提示词并生成回答
formatted_prompt = prompt.format(
    example_context=most_similar_example["context"],
    example_answer=most_similar_example["answer"],
    input_context=current_issue
)

print(formatted_prompt)

response = llm.invoke(formatted_prompt)
print(response)
