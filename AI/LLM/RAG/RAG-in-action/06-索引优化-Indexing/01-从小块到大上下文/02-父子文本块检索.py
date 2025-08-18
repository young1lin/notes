from langchain_deepseek import ChatDeepSeek 
from langchain_huggingface import HuggingFaceEmbeddings 
# 初始化语言模型和向量嵌入模型
llm = ChatDeepSeek(model="deepseek-chat", temperature=0.1)
embed_model = HuggingFaceEmbeddings(model_name="BAAI/bge-small-zh")
# 准备游戏知识文本，创建Document对象。
from langchain.schema import Document
game_knowledge = """
《灭神纪∙猢狲》是一款动作角色扮演游戏。游戏背景设定在架空的神话世界中。玩家将扮演齐天大圣孙悟空，在充满东方神话元素的世界中展开冒险。游戏的战斗系统极具特色，采用了独特的"变身系统"。悟空可以在战斗中变换不同形态。每种形态都有其独特的战斗风格和技能组合。金刚形态侧重力量型打击，带来压倒性的破坏力。魔佛形态则专注法术攻击，能释放强大的法术伤害。游戏世界中充满了标志性的神话角色，除了主角孙悟空以外，还有来自佛教、道教等各派系的神魔。这些角色既可能是悟空的盟友，也可能是需要击败的强大对手。装备系统包含了丰富的武器选择，除了著名的如意金箍棒以外，悟空还可以使用各种神器法宝。不同武器有其特色效果，玩家需要根据战斗场景灵活选择。游戏的画面表现极具东方美学特色，场景融合了水墨画风格，将山川、建筑等元素完美呈现。战斗特效既有中国传统文化元素，又具备现代游戏的视觉震撼力。难度设计上，Boss战充满挑战性，需要玩家精准把握战斗节奏和技能运用。同时游戏也提供了多种难度选择，照顾不同技术水平的玩家。

"""
# 创建Document对象
documents = [Document(page_content=game_knowledge)]
from langchain_text_splitters import RecursiveCharacterTextSplitter
# 父文本块分割器（较大的文本块）
parent_splitter = RecursiveCharacterTextSplitter(
    chunk_size=1000,
    chunk_overlap=200,
    separators=["\n\n", "\n", "。", "！", "？", "；", ",", " ", ""]
)
# 子文本块分割器（较小的文本块）
child_splitter = RecursiveCharacterTextSplitter(
    chunk_size=200,
    chunk_overlap=50,
    separators=["\n\n", "\n", "。", "！", "？", "；", ",", " ", ""]
)
# 创建父子文本块
parent_docs = parent_splitter.split_documents(documents)
child_docs = child_splitter.split_documents(documents)
# 创建存储和检索器，建立两层存储系统
from langchain.retrievers import ParentDocumentRetriever # 父文档检索器
from langchain.storage import InMemoryStore # 内存存储
from langchain_community.vectorstores import Chroma # 向量存储
vectorstore = Chroma(
    collection_name="game_knowledge",
    embedding_function=embed_model
)
store = InMemoryStore()
retriever = ParentDocumentRetriever(
    vectorstore=vectorstore, # 向量存储
    docstore=store, # 文档存储
    child_splitter=child_splitter, # 子文本块分割器
    parent_splitter=parent_splitter, # 父文本块分割器
)
# 添加文本块
retriever.add_documents(documents)
# 自定义提示模板
from langchain.prompts import PromptTemplate
from langchain.chains import RetrievalQA
prompt_template = """基于以下上下文信息回答问题。如果无法找到答案，请说“我找不到相关信息”。
上下文：
{context}
问题：{question}
回答："""
PROMPT = PromptTemplate(
    template=prompt_template,
    input_variables=["context", "question"]
)
# 创建问答链
qa_chain = RetrievalQA.from_chain_type(
    llm=llm,
    chain_type="stuff", # 问答链类型
    retriever=retriever,# 检索器
    return_source_documents=True, # 是否返回源文档
    chain_type_kwargs={"prompt": PROMPT}
)
# 通过实际问答测试系统
test_questions = [
    "游戏中悟空有哪些形态变化？",
    "游戏的画面风格是怎样的？",
]
for question in test_questions:
    print(f"\n问题：{question}")
    result = qa_chain({"query": question})    
    print(f"\n回答：{result['result']}")
    print("\n使用的源文档：")
    for i, doc in enumerate(result["source_documents"], 1):
        print(f"\n相关文档 {i}:")
        print(f"长度：{len(doc.page_content)} 字符")
        print(f"内容片段：{doc.page_content[:150]}...")
        print("---")
