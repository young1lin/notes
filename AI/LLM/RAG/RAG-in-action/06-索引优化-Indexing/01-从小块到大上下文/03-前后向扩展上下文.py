from llama_index.core import VectorStoreIndex, StorageContext, Document, Settings
from llama_index.core.node_parser import SentenceSplitter
from llama_index.core.storage.docstore import SimpleDocumentStore
from llama_index.core.postprocessor import PrevNextNodePostprocessor, AutoPrevNextNodePostprocessor
from llama_index.llms.deepseek import DeepSeek
from llama_index.embeddings.huggingface import HuggingFaceEmbedding
# 配置全局设置
Settings.llm = DeepSeek(model="deepseek-chat", temperature=0.1)
Settings.embed_model = HuggingFaceEmbedding(model_name="BAAI/bge-small-zh")
Settings.node_parser = SentenceSplitter()
# 准备游戏剧情文本
game_story = """悟空初醒时，发现自己被困在一座古老的山洞中。记忆模糊的他只记得自己是齐天大圣孙悟空，却想不起为何会在此处。洞中有一面破碎的镜子，透过镜子他看到自己伤痕累累，昔日的金箍棒也只剩下一截断柄。离开山洞后，悟空遇到了一位神秘的老僧。老僧告诉他，这里是“幻界”，是介于现实与虚幻之间的特殊空间。500年前，天庭遭遇了前所未有的浩劫，众神陨落，天界崩塌。当时正在大闹天宫的悟空也被卷入其中，失去了大部分法力和记忆，被封印在这个世界。老僧建议悟空去寻找散落在幻界各处的记忆碎片。第一站是位于东方的忘川寺，那里供奉着一面记忆之镜，或许能帮他找回部分记忆。然而，忘川寺已被一群邪魔占领，悟空需要先击败它们。在忘川寺，悟空通过记忆之镜看到了天庭浩劫的部分场景。原来是一个神秘的古老势力在背后操纵，他们利用了“众生之愿”的力量，扭曲了天地规则。当时的悟空虽然强大，却也无法阻止灾难的发生。获得这些记忆后，老僧告诉悟空下一站应该前往西方的业火山。那里有一支蜕变的魔族，他们掌握着更多真相。但业火山常年被熊熊烈火包围，普通生灵难以靠近。悟空需要先找到传说中的三昧火甲，才能安全进入。在寻找三昧火甲的过程中，悟空遇到了昔日的好友妖王。妖王告诉他，天庭崩塌后，六界秩序大乱，各方势力纷纷崛起。有的打着重建天庭的旗号，有的则想建立全新的秩序。一场更大的劫难正在酝酿。获得三昧火甲后，悟空成功潜入业火山。在与魔族首领的对决中，他终于想起了更多真相。原来那个古老势力的目标并非简单的破坏，而是想要重塑整个世界的规则。他们认为现有的秩序存在根本缺陷，导致众生皆苦。回到老僧身边，悟空表示要集结各方力量对抗那个幕后势力。老僧却告诉他，事情可能没有表面看起来那么简单。是否应该重塑世界秩序，这个问题并没有标准答案。建议悟空继续寻找更多真相，再做决定。悟空决定启程前往南方的沉星海。传说那里有一座古老的图书馆，收藏着关于世界起源的众多典籍。然而，在他出发前，幻界突然发生剧烈震动，似乎有什么巨大的变故即将发生……"""
# 创建Document对象
documents = [Document(text=game_story)]
# 构建文档存储和索引，并使用Settings中的node_parser解析文档
nodes = Settings.node_parser.get_nodes_from_documents(documents)
# 创建文档存储并添加节点
docstore = SimpleDocumentStore()
docstore.add_documents(nodes)
# 创建存储上下文
storage_context = StorageContext.from_defaults(docstore=docstore)
# 构建向量索引
index = VectorStoreIndex(nodes, storage_context=storage_context)
# 创建不同的查询引擎
# 基础查询引擎
base_engine = index.as_query_engine(
    similarity_top_k=1,
    response_mode="tree_summarize"
)
# 带固定前后文的查询引擎
prev_next_engine = index.as_query_engine(
    similarity_top_k=1,
    node_postprocessors=[
        PrevNextNodePostprocessor(docstore=docstore, num_nodes=2)
    ],
    response_mode="tree_summarize"
)
# 带自动前后文的查询引擎
auto_engine = index.as_query_engine(
    similarity_top_k=1,
    node_postprocessors=[
        AutoPrevNextNodePostprocessor(
            docstore=docstore,
            num_nodes=3,
            verbose=True
        )
    ],
    response_mode="tree_summarize"
)
# 测试不同类型的问题及不同的查询引擎
test_questions = [
    "悟空从忘川寺获得记忆后发生了什么？",  # 应该找后文
    "悟空是如何到达业火山的？",  # 应该找前文
    "悟空为什么会在山洞中醒来？",  # 应该找前文
]
print("=== 基础查询引擎的结果 ===")
for question in test_questions:
    print(f"\n问题：{question}")
    response = base_engine.query(question)
    print(f"回答：{response}\n")
    print("-" * 50)
print("\n=== 固定前后文查询引擎的结果 ===")
for question in test_questions:
    print(f"\n问题：{question}")
    response = prev_next_engine.query(question)
    print(f"回答：{response}\n")
    print("-" * 50)
print("\n=== 自动前后文查询引擎的结果 ===")
for question in test_questions:
    print(f"\n问题：{question}")
    response = auto_engine.query(question)
    print(f"回答：{response}\n")
print("-" * 50)
