# 安装依赖：pip install pymilvus
from pymilvus import MilvusClient, DataType

# ——————————————
# 0. 连接 Milvus
# ——————————————
client = MilvusClient(
    uri="http://localhost:19530",
    token="root:Milvus"
)
print("✓ 已连接 Milvus接口")

# ——————————————
# 1. 创建基本 Schema
# ——————————————
schema = MilvusClient.create_schema()
print("✓ 已创建空 Schema")

# ——————————————
# 2. 添加主键字段（Primary Field）
# ——————————————
# 2.1 INT64类型主键（手动指定ID）
schema.add_field(
    field_name="id",
    datatype=DataType.INT64,
    is_primary=True,  # 设置为主键
    auto_id=False     # 不自动生成ID
)

# 2.2 VARCHAR类型主键（自动生成ID）
# schema.add_field(
#     field_name="doc_id",
#     datatype=DataType.VARCHAR,
#     is_primary=True,  # 设置为主键
#     auto_id=True,     # 自动生成ID
#     max_length=100    # VARCHAR类型需要指定最大长度
# )
print("✓ 已添加主键字段")

# ——————————————
# 3. 添加向量字段（Vector Field）
# ——————————————
# 3.1 Dense Vector (浮点向量)
schema.add_field(
    field_name="text_vector",
    datatype=DataType.FLOAT_VECTOR,  # 32位浮点向量
    dim=768                          # 向量维度
)

# 3.2 Binary Vector (二进制向量)
schema.add_field(
    field_name="image_vector",
    datatype=DataType.BINARY_VECTOR,  # 二进制向量
    dim=256                           # 维度必须是8的倍数
)
print("✓ 已添加向量字段")

# ——————————————
# 4. 添加标量字段（Scalar Field）
# ——————————————
# 4.1 字符串字段
schema.add_field(
    field_name="title",
    datatype=DataType.VARCHAR,
    max_length=200,
    # 可以为空且有默认值
    is_nullable=True,
    default_value="untitled"
)

# 4.2 数值字段
schema.add_field(
    field_name="age",
    datatype=DataType.INT32,
    is_nullable=False  # 不可为空
)

# 4.3 布尔字段
schema.add_field(
    field_name="is_active",
    datatype=DataType.BOOL,
    default_value=True  # 默认值为True
)

# 4.4 JSON字段
schema.add_field(
    field_name="metadata",
    datatype=DataType.JSON
)

# 4.5 数组字段
schema.add_field(
    field_name="tags",
    datatype=DataType.ARRAY,
    element_type=DataType.VARCHAR,  # 数组元素类型
    max_capacity=10,                # 数组最大容量
    max_length=50                   # 每个元素最大长度
)
print("✓ 已添加标量字段")

# ——————————————
# 5. 添加动态字段（Dynamic Field）
# ——————————————
# schema.add_field(
#     field_name="dynamic_field",
#     datatype=DataType.VARCHAR,
#     is_dynamic=True,    # 设置为动态字段
#     max_length=500
# )
print("✓ 已添加动态字段")

# ——————————————
# 6. 使用Schema创建Collection
# ——————————————
collection_name = "document_store10"
client.create_collection(
    collection_name=collection_name,
    schema=schema
)
print(f"✓ 已创建集合 {collection_name}")

# ——————————————
# 7. 修改Collection字段
# ——————————————
# 添加新字段
# client.alter_collection_field(
#     collection_name=collection_name,
#     field_name="tags",
#     field_params={
#         "max_capacity": 64
#     }
# )
# print("✓ 已添加新字段")

# ——————————————
# 8. 查看Collection详情
# ——————————————
info = client.describe_collection(collection_name=collection_name)
print("Collection详情：", info)

# ——————————————
# 9. 清理
# ——————————————
client.drop_collection(collection_name=collection_name)
print("✓ 已删除测试集合")
