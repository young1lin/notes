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
client = MilvusClient("richman_bge_m3.db")

# 1. 创建summary向量数据库
summary_collection_name = "billionaires_summary"
summary_fields = [
    FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
    FieldSchema(name="vector", dtype=DataType.FLOAT_VECTOR, dim=1024),
    FieldSchema(name="summary", dtype=DataType.VARCHAR, max_length=500),
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
    FieldSchema(name="rank", dtype=DataType.INT64),
    FieldSchema(name="name", dtype=DataType.VARCHAR, max_length=100),
    FieldSchema(name="wealth", dtype=DataType.VARCHAR, max_length=100),
    FieldSchema(name="company", dtype=DataType.VARCHAR, max_length=200),
    FieldSchema(name="industry", dtype=DataType.VARCHAR, max_length=100)
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
            logging.info(f"列名: {df.columns.tolist()}")
            
            # 标准化列名（移除换行符和多余的空格）
            df.columns = [col.replace('\n', ' ').strip() for col in df.columns]
            
            # 使用新的列名格式
            if 'Net_Worth' not in df.columns or 'Name' not in df.columns:
                raise ValueError(f"找不到必要的列: Net_Worth 或 Name")
            
            # 插入summary数据
            summary_embedding = embedding_function.encode([sheet_name])[0]
            
            client.insert(
                collection_name=summary_collection_name,
                data=[{
                    "vector": summary_embedding.tolist(),
                    "summary": sheet_name,
                    "table_name": sheet_name
                }]
            )
            
            # 插入details数据
            for _, row in df.iterrows():
                # 清理和格式化数据
                name = str(row['Name']).strip()
                wealth = str(row['Net_Worth']).strip()
                nationality = str(row['Nationality']).strip()
                source = str(row['Source']).strip()
                
                detail_text = f"{name} {wealth} {nationality} {source}"
                detail_embedding = embedding_function.encode([detail_text])[0]
                
                client.insert(
                    collection_name=details_collection_name,
                    data=[{
                        "vector": detail_embedding.tolist(),
                        "table_name": sheet_name,
                        "rank": int(row['No']),
                        "name": name,
                        "wealth": wealth,
                        "company": source,
                        "industry": nationality
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
    
    # 使用正确的search参数格式
    summary_results = client.search(
        collection_name=summary_collection_name,
        data=[query_embedding.tolist()],
        limit=1,
        output_fields=["summary", "table_name"],
        search_params={
            "metric_type": "COSINE",
            "params": {"nprobe": 10}
        }
    )
    
    # 调试输出
    logging.info(f"Summary search results: {summary_results}")
    
    if not summary_results or not summary_results[0]:
        return None, None
    
    # 检查结果结构并提取字段
    try:
        result_item = summary_results[0][0]
        logging.info(f"Result item keys: {result_item.keys()}")
        matched_summary = result_item.get('entity', {}).get('summary') or result_item.get('summary')
        matched_table = result_item.get('entity', {}).get('table_name') or result_item.get('table_name')
        
        if not matched_summary or not matched_table:
            logging.error(f"无法从结果中提取summary或table_name: {result_item}")
            return None, None
    except Exception as e:
        logging.error(f"处理summary结果时出错: {str(e)}")
        return None, None
    
    # 第二层检索：在details集合中搜索具体信息
    details_results = client.search(
        collection_name=details_collection_name,
        data=[query_embedding.tolist()],
        filter=f"table_name == '{matched_table}'",
        limit=5,
        output_fields=["rank", "name", "wealth", "company", "industry"],
        search_params={
            "metric_type": "COSINE",
            "params": {"nprobe": 10}
        }
    )
    
    # 调试输出
    logging.info(f"Details search results structure: {details_results}")
    
    return matched_summary, details_results

def generate_answer(question):
    # 检索相关信息
    summary, details = search_relevant_table(question)
    
    if not summary or not details:
        return "抱歉，没有找到相关信息。"
    
    # 构建提示词
    details_text = "\n".join([
        f"排名：{result.get('entity', {}).get('rank')}, 姓名：{result.get('entity', {}).get('name')}, 财富：{result.get('entity', {}).get('wealth')}, "
        f"公司：{result.get('entity', {}).get('company')}, 行业：{result.get('entity', {}).get('industry')}"
        for result in details[0]
    ])
    
    prompt = f"""根据以下参考信息回答问题：
    
表格说明：{summary}

相关数据：
{details_text}

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