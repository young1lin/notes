from llama_index.core import VectorStoreIndex, Settings
from llama_index.core.schema import IndexNode, Document
from llama_index.llms.deepseek import DeepSeek 
from llama_index.embeddings.huggingface import HuggingFaceEmbedding
from llama_index.core.retrievers import RecursiveRetriever
from llama_index.core.query_engine import RetrieverQueryEngine
from llama_index.core import get_response_synthesizer
from typing import List
# 配置全局设置
Settings.llm = DeepSeek(model="deepseek-chat", temperature=0.1)
Settings.embed_model = HuggingFaceEmbedding(model_name="BAAI/bge-small-zh")
# 创建游戏场景的描述（主文档）
scene_descriptions = [
    """
    花果山：这里是齐天大圣孙悟空的出生地。山上常年缭绕着仙气，瀑布从千米高空倾泻而下，
    形成“天河飞瀑”。山中生长着各种仙草灵药，还有不少修炼成精的动物。
    """,
    """
    水帘洞：位于花果山之巅，洞前有一道天然形成的水帘，既是天然屏障，也是修炼圣地。
    """,
    """
    东海龙宫：位于东海海底的宏伟宫殿，由珊瑚和夜明珠装饰。这里是孙悟空取定海神针的地方。
    """
]
# 将场景描述转换为Document对象
documents = [Document(text=desc) for desc in scene_descriptions]
# 使用节点解析器将文档转换为节点
doc_nodes = Settings.node_parser.get_nodes_from_documents(documents)
# 创建表示层次关系的IndexNode
# 创建场景详细信息（模拟有细节的文档）
scene_details = [
    """
    花果山详细设定
    1. 地理位置：东胜神洲傲来国境内
    2. 自然环境：终年不谢的奇花异草，清澈的山泉和瀑布，茂密的古树森林
    3. 特殊区域：仙果园，种植各种灵果；练功场，平坦开阔的修炼区域；休憩区，供猴族休息的场所
    """,
    """
    水帘洞详细设定
    1. 建筑结构：外部，巨大的天然岩石洞窟；入口，高30丈的水帘瀑布；内部，错综复杂的洞穴系统
    2. 功能分区：修炼大厅，配备各类修炼器具；藏宝室，存放各种法宝和丹药，有强大的防护阵法；议事厅，可容纳数百猴族，商讨重要事务的地方。
    """,
    """
    东海龙宫详细设定
    1. 建筑特征：材质，珊瑚、珍珠、夜明珠；规模，占地数十里；风格，海底宫殿建筑群。
    2. 重要场所：龙王宝库，储存着无数珍宝，如夜明珠，也存放镇海神针等神器；兵器库，各式水系法器，各种神兵利器；大殿，会见宾客的正殿，可召开水族会议。
    """
]
# 为每个详细信息创建对应的IndexNode，并创建对应的查询引擎
index_nodes = []
index_id_query_engine_mapping = {}
for idx, detail_text in enumerate(scene_details):
    # 创建IndexNode，先处理文本再放入f-string
    index_id = f"detail{idx}"
    first_line = detail_text.split('\n')[1].strip()
    index_node = IndexNode(text=f"该节点包含{first_line}", index_id=index_id)
    index_nodes.append(index_node)    
    # 创建对应的TextNode，并构建单独的索引和查询引擎
    detail_node = Document(text=detail_text)
    detail_index = VectorStoreIndex.from_documents([detail_node])
    detail_query_engine = detail_index.as_query_engine()    
    # 将查询引擎添加到映射中
    index_id_query_engine_mapping[index_id] = detail_query_engine    
    # 输出当前的映射情况
    print(f"\n当前索引ID：{index_id}")
    print(f"索引节点文本：{index_node.text}")   
    print(f"对应的场景详细信息长度：{len(detail_text)} 字符")
    print(f"查询引擎类型：{type(detail_query_engine).__name__}")
print("-" * 30)

# 合并文档节点和索引节点
all_nodes = doc_nodes + index_nodes
# 构建主向量索引
vector_index = VectorStoreIndex(all_nodes)
vector_retriever = vector_index.as_retriever(similarity_top_k=2)
# 创建RecursiveRetriever对象
recursive_retriever = RecursiveRetriever(
    "vector",  # 根检索器的 ID
    retriever_dict={"vector": vector_retriever},  # 检索器映射
    query_engine_dict=index_id_query_engine_mapping,  # 查询引擎映射
    verbose=True,  # 启用详细输出
)
# 创建RetrieverQueryEngine，设置响应模式为"compact"
response_synthesizer = get_response_synthesizer(response_mode="compact")
# 创建RetrieverQueryEngine，随后传入RecursiveRetriever和响应合成器
query_engine = RetrieverQueryEngine.from_args(
    recursive_retriever,
    response_synthesizer=response_synthesizer,
)
# 定义查询函数
def query_scene(question: str):
    print(f"问题：{question}\n")
    response = query_engine.query(question)
    print(f"回答：{str(response)}\n")
    print("-" * 50)
# 示例查询
if __name__ == "__main__":
    questions = [
        "花果山里有什么特别的地方？",
        "详细描述一下水帘洞的内部结构。",
        "东海龙宫存放了哪些宝物？",
    ]
    
    for q in questions:
        query_scene(q)
