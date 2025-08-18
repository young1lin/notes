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

def get_database_schema():
    """查询数据库的元数据信息"""
    with driver.session() as session:
        # 查询节点标签
        node_labels_query = """
        CALL db.labels() YIELD label
        RETURN label
        """
        node_labels = session.run(node_labels_query).data()
        
        # 查询关系类型
        relationship_types_query = """
        CALL db.relationshipTypes() YIELD relationshipType
        RETURN relationshipType
        """
        relationship_types = session.run(relationship_types_query).data()
        
        # 查询每个标签的属性
        properties_by_label = {}
        for label in node_labels:
            properties_query = f"""
            MATCH (n:{label['label']})
            WITH n LIMIT 1
            RETURN keys(n) as properties
            """
            properties = session.run(properties_query).data()
            if properties:
                properties_by_label[label['label']] = properties[0]['properties']
        
        return {
            "node_labels": [label['label'] for label in node_labels],
            "relationship_types": [rel['relationshipType'] for rel in relationship_types],
            "properties_by_label": properties_by_label
        }

# 获取数据库结构
schema_info = get_database_schema()
print("\n数据库结构信息：")
print("节点类型：", schema_info["node_labels"])
print("关系类型：", schema_info["relationship_types"])
print("\n节点属性：")
for label, properties in schema_info["properties_by_label"].items():
    print(f"{label}: {properties}")

# 准备SNOMED CT Schema描述
schema_description = f"""
你正在访问一个SNOMED CT图数据库，主要包含以下节点和关系：

节点类型：
{', '.join(schema_info["node_labels"])}

关系类型：
{', '.join(schema_info["relationship_types"])}

节点属性：
"""
for label, properties in schema_info["properties_by_label"].items():
    schema_description += f"\n{label}节点属性：{', '.join(properties)}"

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

请生成Cypher查询语句，注意以下几点：
1. 关系方向要正确，例如：
   - ObjectConcept 拥有 Description，所以应该是 (oc:ObjectConcept)-[:HAS_DESCRIPTION]->(d:Description)
   - 不要写成 (d:Description)-[:HAS_DESCRIPTION]->(oc:ObjectConcept)
2. 使用MATCH子句来匹配节点和关系
3. 使用WHERE子句来过滤条件，建议使用toLower()函数进行不区分大小写的匹配
4. 使用RETURN子句来指定返回结果
5. 请只返回Cypher查询语句，不要包含任何其他解释、注释或格式标记（如```cypher）
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
    print(f"\n查询结果：{results}")

# 关闭数据库连接
driver.close() 