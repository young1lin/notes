# text2sql_query.py
import os
import logging
import yaml
import openai
from dotenv import load_dotenv
from pymilvus import MilvusClient
from pymilvus import model
from sqlalchemy import create_engine, text

# 1. 环境与日志配置
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
load_dotenv()  # 加载 .env 环境变量

# 2. 初始化 OpenAI API（使用最新 Response API）
openai.api_key = os.getenv("OPENAI_API_KEY")

# 建议使用新 Response API 风格
# 例如: openai.chat.completions.create(...) 而非旧的 ChatCompletion.create

MODEL_NAME = os.getenv("OPENAI_MODEL", "gpt-4o")

# 3. 嵌入函数初始化
def init_embedding():
    return model.dense.OpenAIEmbeddingFunction(
        model_name='text-embedding-3-large',
    )

# 4. Milvus 客户端连接
MILVUS_DB = os.getenv("MILVUS_DB_PATH", "text2sql_milvus_sakila.db")
client = MilvusClient(MILVUS_DB)

# 5. 嵌入函数实例化
embedding_fn = init_embedding()

# 6. 数据库连接（SAKILA）
DB_URL = os.getenv(
    "SAKILA_DB_URL", 
    "mysql+pymysql://root:password@localhost:3306/sakila"
)
engine = create_engine(DB_URL)

# 7. 检索函数
def retrieve(collection: str, query_emb: list, top_k: int = 3, fields: list = None):
    results = client.search(
        collection_name=collection,
        data=[query_emb],
        limit=top_k,
        output_fields=fields
    )
    logging.info(f"[检索] {collection} 检索结果: {results}")
    return results[0]  # 返回第一个查询的结果列表

# 8. 核心流程：自然语言 -> SQL -> 执行 -> 返回
def text2sql(question: str):
    # 8.1 用户提问嵌入
    q_emb = embedding_fn([question])[0]
    logging.info(f"[检索] 问题嵌入完成")

    # 8.2 RAG 检索：DDL
    ddl_hits = retrieve("ddl_knowledge", q_emb.tolist(), top_k=3, fields=["ddl_text"])
    logging.info(f"[检索] DDL检索结果: {ddl_hits}")
    try:
        ddl_context = "\n".join(hit.get("ddl_text", "") for hit in ddl_hits)
    except Exception as e:
        logging.error(f"[检索] DDL处理错误: {e}")
        ddl_context = ""

    # 8.3 RAG 检索：示例对
    q2sql_hits = retrieve("q2sql_knowledge", q_emb.tolist(), top_k=3, fields=["question", "sql_text"])
    logging.info(f"[检索] Q2SQL检索结果: {q2sql_hits}")
    try:
        example_context = "\n".join(
            f"NL: \"{hit.get('question', '')}\"\nSQL: \"{hit.get('sql_text', '')}\"" 
            for hit in q2sql_hits
        )
    except Exception as e:
        logging.error(f"[检索] Q2SQL处理错误: {e}")
        example_context = ""

    # 8.4 RAG 检索：字段描述
    desc_hits = retrieve("dbdesc_knowledge", q_emb.tolist(), top_k=5, fields=["table_name", "column_name", "description"])
    logging.info(f"[检索] 字段描述检索结果: {desc_hits}")
    try:
        desc_context = "\n".join(
            f"{hit.get('table_name', '')}.{hit.get('column_name', '')}: {hit.get('description', '')}"
            for hit in desc_hits
        )
    except Exception as e:
        logging.error(f"[检索] 字段描述处理错误: {e}")
        desc_context = ""

    # 8.5 Prompt 组装
    prompt = (
        f"### Schema Definitions:\n{ddl_context}\n"
        f"### Field Descriptions:\n{desc_context}\n"
        f"### Examples:\n{example_context}\n"
        f"### Query:\n\"{question}\"\nSQL:"
    )
    logging.info("[生成] 开始生成SQL")

    # 8.6 调用最新 Response API
    response = openai.chat.completions.create(
        model=MODEL_NAME,
        messages=[{"role": "user", "content": prompt}],
        temperature=0
    )
    sql = response.choices[0].message.content.strip()
    logging.info(f"[生成] 生成的SQL: {sql}")

    # 8.7 执行并打印结果
    try:
        with engine.connect() as conn:
            result = conn.execute(text(sql))
            cols = result.keys()
            rows = result.fetchall()
            print("\n查询结果：")
            print("列名：", cols)
            for r in rows:
                print(r)
    except Exception as e:
        logging.error(f"[执行] 执行失败: {e}")
        print("执行错误：", e)

# 9. 程序入口
if __name__ == "__main__":
    user_q = input("请输入您的自然语言查询： ")
    text2sql(user_q)