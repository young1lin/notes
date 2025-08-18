import os
from dotenv import load_dotenv
from sentence_transformers import SentenceTransformer
import torch
from pymilvus import MilvusClient
import logging

# 设置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# 加载环境变量
load_dotenv()

# 初始化嵌入模型
embedding_function = SentenceTransformer(
    'BAAI/bge-m3',
    device='cuda:0' if torch.cuda.is_available() else 'cpu',
    trust_remote_code=True
)

# 连接到Milvus
client = MilvusClient("richman_bge_m3_v2.db")

def search_relevant_table(question):
    # 第一层检索：在summary集合中搜索最相关的sheet
    query_embedding = embedding_function.encode([question])[0]
    
    summary_results = client.search(
        collection_name="billionaires_summary",
        data=[query_embedding.tolist()],
        limit=1,
        output_fields=["table_name"],
        search_params={
            "metric_type": "COSINE",
            "params": {"nprobe": 10}
        }
    )
    
    if not summary_results or not summary_results[0]:
        return None, None
    
    matched_table = summary_results[0][0]['entity']['table_name']
    
    # 第二层检索：在details集合中搜索具体信息
    details_results = client.search(
        collection_name="billionaires_details",
        data=[query_embedding.tolist()],
        filter=f"table_name == '{matched_table}'",
        limit=1,
        output_fields=["content"],
        search_params={
            "metric_type": "COSINE",
            "params": {"nprobe": 10}
        }
    )
    
    if not details_results or not details_results[0]:
        return None, None
    
    return matched_table, details_results[0][0]['entity']['content']

def generate_answer(question):
    # 检索相关信息
    table_name, content = search_relevant_table(question)
    
    if not table_name or not content:
        return "抱歉，没有找到相关信息。"
    
    # 构建提示词
    prompt = f"""根据以下表格信息回答问题：

表格名称：{table_name}

表格内容：
{content}

问题：{question}

请基于以上信息给出详细回答："""

    # 使用DeepSeek生成答案
    from openai import OpenAI
    client = OpenAI(
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        base_url="https://api.deepseek.com/v1"
    )

    response = client.chat.completions.create(
        model="deepseek-chat",
        messages=[{
            "role": "user",
            "content": prompt
        }],
        max_tokens=1024
    )
    
    return response.choices[0].message.content

# 测试查询列表
test_queries = [
    # 基本查询
    "2023年世界首富是谁？他的财富是多少？",
    "2020年世界首富是谁？他的财富是多少？",
    "2022年世界富豪榜前十名中有多少位来自美国？",
    "2021年世界富豪榜前十名中有多少位来自中国？",
    
    # 比较查询
    "2020年世界首富和第二富豪的财富差距是多少？",
    "2019年世界富豪榜前十名中，科技行业和奢侈品行业的富豪数量对比如何？",
    
    # 趋势查询
    "2019年世界富豪榜前十名中，科技行业富豪的财富占比是多少？",
    "2022年世界富豪榜前十名中，年龄最大的富豪是谁？",
    
    # 复杂查询
    "2022年世界富豪榜前十名中，来自欧洲的富豪主要从事哪些行业？",
    "2021年世界富豪榜前十名中，财富来源为科技行业的富豪平均年龄是多少？"
]

# 运行测试
if __name__ == "__main__":
    print("开始测试双层RAG系统...\n")
    
    for i, query in enumerate(test_queries, 1):
        print(f"\n测试 {i}/{len(test_queries)}")
        print(f"问题：{query}")
        print("-" * 50)
        
        answer = generate_answer(query)
        print(f"答案：{answer}")
        print("-" * 50) 