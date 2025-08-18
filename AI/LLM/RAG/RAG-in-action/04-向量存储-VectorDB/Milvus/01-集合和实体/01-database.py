# 安装依赖：pip install pymilvus
# pip show pymilvus  # 查看当前 SDK 版本
'''
# 安装Milvus服务端

wget https://github.com/milvus-io/milvus/releases/download/v2.5.10/milvus-standalone-docker-compose.yml -O docker-compose.yml

sudo docker compose up -d

Creating milvus-etcd  ... done
Creating milvus-minio ... done
Creating milvus-standalone ... done

'''

from pymilvus import MilvusClient, exceptions

# ——————————————
# 1. 连接 Milvus Standalone
# ——————————————
# uri: 协议+地址+端口，默认为 http://localhost:19530
# token: "用户名:密码"，默认 root:Milvus
client = MilvusClient(
    uri="http://localhost:19530",
    token="root:Milvus"
)

# ——————————————
# 2. 创建数据库 my_database_1（无额外属性）
# ——————————————
try:
    client.create_database(db_name="my_database_1")
    print("✓ my_database_1 创建成功")
except exceptions.AlreadyExistError:
    print("ℹ my_database_1 已存在")

# ——————————————
# 3. 创建数据库 my_database_2（设置副本数为 3）
# ——————————————
client.create_database(
    db_name="my_database_2",
    properties={"database.replica.number": 3}
)
print("✓ my_database_2 创建成功，副本数=3")

# ——————————————
# 4. 列出所有数据库
# ——————————————
db_list = client.list_databases()
print("当前所有数据库：", db_list)

# ——————————————
# 5. 查看默认数据库（default）详情
# ——————————————
default_info = client.describe_database(db_name="default")
print("默认数据库详情：", default_info)

# ——————————————
# 6. 修改 my_database_1 属性：限制最大集合数为 10
# ——————————————
client.alter_database_properties(
    db_name="my_database_1",
    properties={"database.max.collections": 10}
)
print("✓ 已为 my_database_1 限制最大集合数为 10")

# ——————————————
# 7. 删除 my_database_1 的 max.collections 限制
# ——————————————
client.drop_database_properties(
    db_name="my_database_1",
    property_keys=["database.max.collections"]
)
print("✓ 已移除 my_database_1 的最大集合数限制")

# ——————————————
# 8. 切换到 my_database_2（后续所有操作都作用于该库）
# ——————————————
client.use_database(db_name="my_database_2")
print("✓ 已切换当前数据库为 my_database_2")

# ——————————————
# 9. 删除数据库 my_database_2
#    （注意：如果库内有 Collection，需先 client.drop_collection() 将其清空）
# ——————————————
client.drop_database(db_name="my_database_2")
print("✓ my_database_2 已删除")

# ——————————————
# 10. 删除数据库 my_database_1
# ——————————————
client.drop_database(db_name="my_database_1")
print("✓ my_database_1 已删除")
