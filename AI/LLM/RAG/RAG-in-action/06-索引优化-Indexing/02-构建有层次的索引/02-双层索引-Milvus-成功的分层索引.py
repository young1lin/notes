# 双层检索-富豪榜 - 需要pip install openpyxl
import os
from dotenv import load_dotenv
import pandas as pd
from sentence_transformers import SentenceTransformer
import torch
from pymilvus import MilvusClient, DataType, FieldSchema, CollectionSchema
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

# 1. 创建summary向量数据库
summary_collection_name = "billionaires_summary"
summary_fields = [
    FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
    FieldSchema(name="vector", dtype=DataType.FLOAT_VECTOR, dim=1024),
    FieldSchema(name="table_name", dtype=DataType.VARCHAR, max_length=100)
]

summary_schema = CollectionSchema(summary_fields, "富豪榜年份摘要")
if not client.has_collection(summary_collection_name):
    client.create_collection(
        collection_name=summary_collection_name,
        schema=summary_schema
    )

# 2. 创建details向量数据库
details_collection_name = "billionaires_details"
details_fields = [
    FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
    FieldSchema(name="vector", dtype=DataType.FLOAT_VECTOR, dim=1024),
    FieldSchema(name="table_name", dtype=DataType.VARCHAR, max_length=100),
    FieldSchema(name="content", dtype=DataType.VARCHAR, max_length=10000)  # 存储整个表格内容
]

details_schema = CollectionSchema(details_fields, "富豪榜详细信息")
if not client.has_collection(details_collection_name):
    client.create_collection(
        collection_name=details_collection_name,
        schema=details_schema
    )

# 3. 加载Excel文件并准备数据
excel_file = "90-文档-Data/复杂PDF/十大富豪/世界十大富豪.xlsx"

# 读取Excel文件中的所有sheet并插入数据
with pd.ExcelFile(excel_file) as xls:
    for sheet_name in xls.sheet_names:
        try:
            df = pd.read_excel(xls, sheet_name=sheet_name)
            logging.info(f"正在处理sheet: {sheet_name}")
            
            # 插入summary数据 - 只存储表名
            summary_embedding = embedding_function.encode([sheet_name])[0]
            
            client.insert(
                collection_name=summary_collection_name,
                data=[{
                    "vector": summary_embedding.tolist(),
                    "table_name": sheet_name
                }]
            )
            
            # 插入details数据 - 存储整个表格内容
            # 将整个DataFrame转换为字符串
            table_content = df.to_string(index=False)
            detail_embedding = embedding_function.encode([table_content])[0]
            
            client.insert(
                collection_name=details_collection_name,
                data=[{
                    "vector": detail_embedding.tolist(),
                    "table_name": sheet_name,
                    "content": table_content
                }]
            )
            
            logging.info(f"成功处理sheet: {sheet_name}")
            
        except Exception as e:
            logging.error(f"处理sheet {sheet_name} 时出错: {str(e)}")
            logging.error(f"错误详情: {e.__class__.__name__}")
            continue

# 4. 创建索引
# 删除已存在的索引（如果有）
try:
    client.drop_index(collection_name=summary_collection_name, index_name="vector")
except Exception as e:
    logging.warning(f"删除summary索引时出错: {str(e)}")

try:
    client.drop_index(collection_name=details_collection_name, index_name="vector")
except Exception as e:
    logging.warning(f"删除details索引时出错: {str(e)}")

# 创建新索引
try:
    # 使用prepare_index_params方法创建索引参数
    summary_index_params = client.prepare_index_params()
    summary_index_params.add_index(
        field_name="vector",  # 指定要为哪个字段创建索引，这里是向量字段
        index_type="IVF_FLAT",  # 索引类型
        metric_type="COSINE",  # 使用余弦相似度作为向量相似度度量方式
        params={"nlist": 1024}  # 索引参数
    )
    
    client.create_index(
        collection_name=summary_collection_name,
        index_params=summary_index_params
    )
    logging.info("成功创建summary索引")
except Exception as e:
    logging.error(f"创建summary索引时出错: {str(e)}")

try:
    # 使用prepare_index_params方法创建索引参数
    details_index_params = client.prepare_index_params()
    details_index_params.add_index(
        field_name="vector",  # 指定要为哪个字段创建索引，这里是向量字段
        index_type="IVF_FLAT",  # 索引类型
        metric_type="COSINE",  # 使用余弦相似度作为向量相似度度量方式
        params={"nlist": 1024}  # 索引参数
    )
    
    client.create_index(
        collection_name=details_collection_name,
        index_params=details_index_params
    )
    logging.info("成功创建details索引")
except Exception as e:
    logging.error(f"创建details索引时出错: {str(e)}")

# 加载集合以使索引生效
try:
    client.load_collection(summary_collection_name)
    client.load_collection(details_collection_name)
    logging.info("成功加载集合")
except Exception as e:
    logging.error(f"加载集合时出错: {str(e)}")

def search_relevant_table(question):
    # 第一层检索：在summary集合中搜索最相关的sheet
    query_embedding = embedding_function.encode([question])[0]
    
    summary_results = client.search(
        collection_name=summary_collection_name,
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
        collection_name=details_collection_name,
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

# 测试示例
if __name__ == "__main__":
    test_question = "2023年世界首富是谁？他的财富是多少？"
    answer = generate_answer(test_question)
    print(f"问题：{test_question}")
    print(f"答案：{answer}") 