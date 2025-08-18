import os
from dotenv import load_dotenv
import pandas as pd
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np
from openai import OpenAI

# 加载环境变量
load_dotenv()

# 1. 准备表格说明数据
table_descriptions = [
    "2023年世界十大富豪榜单，展示了当年全球最富有的十位富豪及其财富情况。",
    "2022年世界十大富豪榜单，记录了当年全球最富有的十位富豪及其财富情况。",
    "2021年世界十大富豪榜单，展示了当年全球最富有的十位富豪及其财富情况。",
    "2020年世界十大富豪榜单，记录了当年全球最富有的十位富豪及其财富情况。",
    "2019年世界十大富豪榜单，展示了当年全球最富有的十位富豪及其财富情况。"
]

# 2. 设置第一层嵌入模型（用于匹配年份）
model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')
desc_embeddings = model.encode(table_descriptions)

# 3. 创建第一层向量存储
dimension = desc_embeddings.shape[1]
desc_index = faiss.IndexFlatL2(dimension)
desc_index.add(desc_embeddings.astype('float32'))

# 4. 加载Excel文件并准备第二层数据
excel_file = "90-文档-Data/复杂PDF/十大富豪/世界十大富豪.xlsx"
all_tables_data = {}

# 读取Excel文件中的所有sheet
with pd.ExcelFile(excel_file) as xls:
    for sheet_name in xls.sheet_names:
        df = pd.read_excel(xls, sheet_name=sheet_name)
        # 将DataFrame转换为文本格式
        table_text = df.to_string(index=False)
        all_tables_data[sheet_name] = table_text

# 5. 创建第二层向量存储
table_embeddings = model.encode(list(all_tables_data.values()))
table_index = faiss.IndexFlatL2(dimension)
table_index.add(table_embeddings.astype('float32'))

def search_relevant_table(question):
    # 第一层检索：匹配年份
    query_embedding = model.encode([question])[0]
    distances, indices = desc_index.search(
        np.array([query_embedding]).astype('float32'), 
        k=1
    )
    matched_year = indices[0][0]
    
    # 第二层检索：在匹配的年份表格中搜索具体信息
    table_embedding = model.encode([all_tables_data[f"billionaires_table_{matched_year+2}"]])[0]
    distances, indices = table_index.search(
        np.array([table_embedding]).astype('float32'), 
        k=1
    )
    
    return table_descriptions[matched_year], all_tables_data[f"billionaires_table_{matched_year+2}"]

def generate_answer(question):
    # 检索相关信息
    year_context, table_context = search_relevant_table(question)
    
    # 构建提示词
    prompt = f"""根据以下参考信息回答问题：
    
年份信息：{year_context}

相关数据：
{table_context}

问题：{question}

请基于以上信息给出详细回答："""

    # 使用DeepSeek生成答案
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