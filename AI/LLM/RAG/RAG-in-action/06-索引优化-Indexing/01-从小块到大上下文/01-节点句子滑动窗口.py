from llama_index.core import  VectorStoreIndex, Settings, Document
from llama_index.core.node_parser import  SentenceWindowNodeParser, SentenceSplitter
from llama_index.llms.deepseek import DeepSeek
from llama_index.embeddings.huggingface import HuggingFaceEmbedding
from llama_index.core.postprocessor import MetadataReplacementPostProcessor # 元数据替换后处理器
# 配置全局设置
Settings.llm = DeepSeek(model="deepseek-chat", temperature=0.1)
Settings.embed_model = HuggingFaceEmbedding(model_name="BAAI/bge-small-zh")
Settings.text_splitter = SentenceSplitter(separator="\n",  chunk_size=50, chunk_overlap=0)
# 准备知识文本并创建Document对象
game_knowledge = """
《灭神纪∙猢狲》是一款动作角色扮演游戏。游戏背景设定在架空的神话世界中。
玩家将扮演齐天大圣孙悟空，在充满东方神话元素的世界中展开冒险。
游戏的战斗系统极具特色，采用了独特的“变身系统”。悟空可以在战斗中变换不同形态。
每种形态都有其独特的战斗风格和技能组合。金刚形态侧重力量型打击，带来压倒性的破坏力。
魔佛形态则专注法术攻击，能释放强大的法术伤害。
游戏世界中充满了标志性的神话角色，除了主角孙悟空以外，还有来自佛教、道教等各派系的神魔。
这些角色既可能是悟空的盟友，也可能是需要击败的强大对手。
装备系统包含了丰富的武器选择，除了著名的如意金箍棒以外，悟空还可以使用各种神器法宝。
不同武器有其特色效果，玩家需要根据战斗场景灵活选择。
游戏的画面表现极具东方美学特色，场景融合了水墨画风格，将山川、建筑等元素完美呈现。
战斗特效既有中国传统文化元素，又具备现代游戏的视觉震撼力。
难度设计上，Boss战充满挑战性，需要玩家精准把握战斗节奏和技能运用。
同时游戏也提供了多种难度选择，照顾不同技术水平的玩家。"""
# 创建Document对象
documents = [Document(text=game_knowledge)]
# 创建带上下文窗口的句子解析器（每个目标句子两侧各保留n个句子作为上下文）
node_parser = SentenceWindowNodeParser.from_defaults(
    window_size=3,
    window_metadata_key="window",
    original_text_metadata_key="original_text"
)
# 使用窗口解析器处理文档
nodes = node_parser.get_nodes_from_documents(documents)
# 使用基础解析器处理文档（用于对比）
base_nodes = Settings.text_splitter.get_nodes_from_documents(documents)
# 构建两种索引用于对比
sentence_index = VectorStoreIndex(nodes)
base_index = VectorStoreIndex(base_nodes)
# 创建带上下文窗口的查询引擎
window_query_engine = sentence_index.as_query_engine(
    similarity_top_k=2,
    node_postprocessors=[
        MetadataReplacementPostProcessor(target_metadata_key="window")
    ]
)
# 创建基础查询引擎
base_query_engine = base_index.as_query_engine(
    similarity_top_k=6
)
# 测试问答
test_questions = [
    "游戏中悟空有哪些形态变化？",
    # "游戏的画面风格是怎样的？",
    # "游戏的难度设计如何？"
]
print("=== 使用窗口解析器的检索结果 ===")
for question in test_questions:
    print(f"\n问题：{question}")
    window_response = window_query_engine.query(question)
    print(f"回答：{window_response}")
    
    # 展示检索到的原始句子和窗口内容
    print("\n检索详情：")
    for node in window_response.source_nodes:
        print(f"原始句子：{node.node.metadata['original_text']}")
        print(f"上下文窗口：{node.node.metadata['window']}")
        print("---")
print("\n=== 使用基础解析器的检索结果（对比）===")
for question in test_questions:
    print(f"\n问题：{question}")
    base_response = base_query_engine.query(question)
print(f"回答：{base_response}")
