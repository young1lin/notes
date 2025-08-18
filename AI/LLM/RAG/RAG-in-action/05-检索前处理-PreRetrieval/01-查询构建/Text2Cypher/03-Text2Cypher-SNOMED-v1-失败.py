# 准备Neo4j数据库连接
from neo4j import GraphDatabase
import os
from dotenv import load_dotenv
load_dotenv()

# Neo4j连接配置
uri = "bolt://localhost:7687"  # 默认Neo4j Bolt端口
username = "neo4j"
password = os.getenv("NEO4J_PASSWORD")  # 从环境变量获取密码

# 初始化Neo4j驱动
driver = GraphDatabase.driver(uri, auth=(username, password))

# 准备SNOMED CT Schema描述
schema_description = """
你正在访问一个SNOMED CT图数据库，主要包含以下节点和关系：

节点类型：
1. Concept (概念节点)
   - conceptId: 概念唯一标识符
   - fullySpecifiedName: 完整概念名称
   - preferredTerm: 首选术语
   - active: 是否激活
   - effectiveTime: 生效时间
   - moduleId: 模块ID

2. Description (描述节点)
   - descriptionId: 描述唯一标识符
   - term: 术语文本
   - typeId: 描述类型ID
   - languageCode: 语言代码
   - active: 是否激活

3. Relationship (关系节点)
   - relationshipId: 关系唯一标识符
   - typeId: 关系类型ID
   - active: 是否激活

关系类型：
1. IS_A: 表示概念之间的层级关系
2. HAS_DESCRIPTION: 概念与其描述之间的关系
3. HAS_RELATIONSHIP: 概念之间的其他关系
"""

# 初始化DeepSeek客户端
from openai import OpenAI
client = OpenAI(
    base_url="https://api.deepseek.com",
    api_key=os.getenv("DEEPSEEK_API_KEY")
)

# 设置查询
user_query = "查找与'Diabetes'相关的所有概念及其描述"

# 准备生成Cypher的提示词
prompt = f"""
以下是SNOMED CT图数据库的结构描述：
{schema_description}
用户的自然语言问题如下：
"{user_query}"
请注意：
1. 使用MATCH子句来匹配节点和关系
2. 使用WHERE子句来过滤条件
3. 使用RETURN子句来指定返回结果
4. 请只返回Cypher查询语句，不要包含任何其他解释、注释或格式标记（如```cypher）
"""

# 调用LLM生成Cypher语句
response = client.chat.completions.create(
    model="deepseek-chat",
    messages=[
        {"role": "system", "content": "你是一个Cypher查询专家。请只返回Cypher查询语句，不要包含任何Markdown格式或其他说明。"},
        {"role": "user", "content": prompt}
    ],
    temperature=0
)

# 清理Cypher语句，移除可能的Markdown标记
cypher = response.choices[0].message.content.strip()
cypher = cypher.replace('```cypher', '').replace('```', '').strip()
print(f"\n生成的Cypher查询语句：\n{cypher}")

# 执行Cypher查询并获取结果
def run_query(tx, query):
    result = tx.run(query)
    return [record for record in result]

with driver.session() as session:
    results = session.execute_read(run_query, cypher)
    print(f"查询结果：{results}")

# 生成自然语言描述
if results:
    nl_prompt = f"""
查询结果如下：
{results}
请将这些数据转换为自然语言描述，使其易于理解。
原始问题是：{user_query}

要求：
1. 使用通俗易懂的语言
2. 包含所有查询到的数据信息
3. 如果有专业术语，请适当解释
"""
    response_nl = client.chat.completions.create(
        model="deepseek-chat",
        messages=[
            {"role": "system", "content": "你是一个医学信息专家，负责将SNOMED CT查询结果转换为易懂的自然语言描述。"},
            {"role": "user", "content": nl_prompt}
        ],
        temperature=0.7
    )    
    description = response_nl.choices[0].message.content.strip()
    print(f"自然语言描述：\n{description}")
else:
    print("未找到相关数据。")

# 关闭数据库连接
driver.close() 