# 安装依赖：pip install pymilvus
from pymilvus import MilvusClient

# ——————————————
# 0. 连接 Milvus
# ——————————————
client = MilvusClient(
    uri="http://localhost:19530",
    token="root:Milvus"
)
print("✓ 已连接 Milvus接口")

# ——————————————
# 1. 创建 Collection（快速模式）
# ——————————————
# 检查并删除已存在的集合
collection_name = "quick_setup"
if collection_name in client.list_collections():
    client.drop_collection(collection_name=collection_name)
    print(f"✓ 已删除已存在的集合 {collection_name}")

# 创建新集合
client.create_collection(
    collection_name=collection_name,
    dimension=5
)
print(f"✓ {collection_name} 已创建")

# ——————————————
# 2. 列出所有 Collections
# ——————————————
cols = client.list_collections()
print("当前所有集合：", cols)

# ——————————————
# 3. 查看 Collection 详情
# ——————————————
info = client.describe_collection(collection_name=collection_name)
print(f"{collection_name} 详情：", info)

# ——————————————
# 4. 重命名 Collection
# ——————————————
new_collection_name = "quick_renamed"
if new_collection_name in client.list_collections():
    client.drop_collection(collection_name=new_collection_name)
    print(f"✓ 已删除已存在的集合 {new_collection_name}")

client.rename_collection(
    old_name=collection_name,
    new_name=new_collection_name
)
print(f"✓ {collection_name} 已重命名为 {new_collection_name}")

# ——————————————
# 5. 修改 Collection 属性（设置 TTL 60 秒）
# ——————————————
client.alter_collection_properties(
    collection_name=new_collection_name,
    properties={"collection.ttl.seconds": 60}
)
print(f"✓ 已为 {new_collection_name} 设置 TTL=60s")

# ——————————————
# 6. 删除 Collection 属性（TTL）
# ——————————————
client.drop_collection_properties(
    collection_name=new_collection_name,
    property_keys=["collection.ttl.seconds"]
)
print(f"✓ 已删除 {new_collection_name} 的 TTL 属性")

# ——————————————
# 7. 加载 & 检查加载状态
# ——————————————
client.load_collection(collection_name=new_collection_name)
state = client.get_load_state(collection_name=new_collection_name)
print("加载状态：", state)

# ——————————————
# 8. 释放 & 检查释放状态
# ——————————————
client.release_collection(collection_name=new_collection_name)
state = client.get_load_state(collection_name=new_collection_name)
print("释放后状态：", state)

# ——————————————
# 9. 管理 Partition
# ——————————————
# 9.1 列出 Partition（默认只有 "_default"）
parts = client.list_partitions(collection_name=new_collection_name)
print("Partition 列表：", parts)

# 9.2 创建新 Partition
client.create_partition(
    collection_name=new_collection_name,
    partition_name="partA"
)
print("✓ 已创建 partition partA")
print("更新后 Partition 列表：", client.list_partitions(new_collection_name))

# 9.3 检查 Partition 是否存在
exists = client.has_partition(
    collection_name=new_collection_name,
    partition_name="partA"
)
print("partA 存在？", exists)

# 9.4 加载 & 释放 指定 Partition
client.load_partitions(
    collection_name=new_collection_name,
    partition_names=["partA"]
)
print("partA 加载状态：", client.get_load_state(new_collection_name, partition_name="partA"))

client.release_partitions(
    collection_name=new_collection_name,
    partition_names=["partA"]
)
print("partA 释放后状态：", client.get_load_state(new_collection_name, partition_name="partA"))

# 9.5 删除 Partition（需先 release）
client.drop_partition(
    collection_name=new_collection_name,
    partition_name="partA"
)
print("✓ 已删除 partition partA")
print("最终 Partition 列表：", client.list_partitions(new_collection_name))

# ——————————————
# 10. 管理 Alias
# ——————————————
# 10.1 创建 Alias
client.create_alias(collection_name=new_collection_name, alias="alias3")
client.create_alias(collection_name=new_collection_name, alias="alias4")
print("✓ 已创建 alias3, alias4")

# 10.2 列出 Alias
aliases = client.list_aliases(collection_name=new_collection_name)
print("当前 aliases：", aliases)

# 10.3 查看 Alias 详情
desc = client.describe_alias(alias="alias3")
print("alias3 详情：", desc)

# 10.4 重新分配 Alias
client.alter_alias(collection_name=new_collection_name, alias="alias4")
print("✓ 已将 alias4 重新分配给 quick_renamed")

# 10.5 删除 Alias
client.drop_alias(alias="alias3")
print("✓ 已删除 alias3")
print("剩余 aliases：", client.list_aliases(new_collection_name))

# ——————————————
# 11. 删除 Collection
# ——————————————
client.drop_collection(collection_name=new_collection_name)
print(f"✓ 集合 {new_collection_name} 已删除")