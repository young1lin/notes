# 准备数据库连接
import sqlite3
conn = sqlite3.connect('data/tourism.db')
cursor = conn.cursor()

# 准备Schema描述
schema_description = """
你正在访问一个包含两张表的数据库：
1. scenic_spots（景区信息表）
   - scenic_id (INT): 主键，景区唯一标识
   - scenic_name (VARCHAR): 景区名称
   - city (VARCHAR): 所在城市
   - level (VARCHAR): 景区等级
   - monthly_visitors (INT): 当月游客量
2. city_info（城市信息表）
   - city_id (INT): 主键，城市唯一标识
   - city_name (VARCHAR): 城市名称
   - annual_tourism_income (INT): 年度文旅收入（单位：元）
   - famous_dish (VARCHAR): 当地名菜/特色小吃
"""

# 初始化OpenAI客户端
from openai import OpenAI
client = OpenAI()

# 设置查询
user_query = "查询太原市的AAAAA级景区及其当月游客量"

# 准备生成SQL的提示词
prompt = f"""
以下是数据库的结构描述：
{schema_description}
用户的自然语言问题如下：
"{user_query}"
请注意：
1. scenic_spots表中的city字段存储的是城市名称，对应city_info表中的city_name
2. 两张表之间的关联应该使用city_name和city进行匹配
3. 请只返回SQL查询语句，不要包含任何其他解释、注释或格式标记（如```sql）
"""
# 调用LLM生成SQL语句
response = client.chat.completions.create(
    model="gpt-4",
    messages=[
        {"role": "system", "content": "你是一个SQL专家。请只返回SQL查询语句，不要包含任何Markdown格式或其他说明。"},
        {"role": "user", "content": prompt}
    ],
    temperature=0
)

# 清理SQL语句，移除可能的Markdown标记
sql = response.choices[0].message.content.strip()
sql = sql.replace('```sql', '').replace('```', '').strip()
print(f"\n生成的SQL查询语句：\n{sql}")

# 执行SQL并获取结果
cursor.execute(sql)
results = cursor.fetchall()
print(f"查询结果：{results}")
conn.close()

# 生成自然语言描述
if results:
    # 获取列名
    column_names = [description[0] for description in cursor.description]
    # 将结果转换为字典列表
    results_with_columns = [dict(zip(column_names, row)) for row in results]    
    nl_prompt = f"""
查询结果如下：
{results_with_columns}
请将这些数据转换为自然语言描述，使其易于理解。
原始问题是：{user_query}

要求：
1. 使用通俗易懂的语言
2. 包含所有查询到的数据信息
3. 如果有数字，请使用中文数字表述
"""
    response_nl = client.chat.completions.create(
        model="gpt-4",
        messages=[
            {"role": "system", "content": "你是一个数据分析师，负责将查询结果转换为易懂的自然语言描述。"},
            {"role": "user", "content": nl_prompt}
        ],
        temperature=0.7
    )    
    description = response_nl.choices[0].message.content.strip()
    print(f"自然语言描述：\n{description}")
else:
    print("未找到相关数据。")
# 关闭数据库连接
conn.close()
