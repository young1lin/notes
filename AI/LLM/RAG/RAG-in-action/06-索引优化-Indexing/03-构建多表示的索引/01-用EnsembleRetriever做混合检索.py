from langchain_huggingface import HuggingFaceEmbeddings
from langchain_deepseek import ChatDeepSeek
from langchain.chains import RetrievalQA
# 系统设定文档：关注具体游戏机制和系统
system_docs = [
    "《灭神纪∙猢狲》采用独特的变身系统作为核心战斗机制",
    "金刚形态下可以使用重型武器，增加攻击力和防御力",
    "魔佛形态专注于法术攻击，可以释放强大的法术伤害",
    "战斗中可以随时切换不同形态，实现连击",
    "游戏难度分为普通、困难和修罗三个等级"
]
# 世界观文档：关注剧情和背景设定
lore_docs = [
    "游戏背景设定在架空的神话世界中，融合东方神话元素",
    "孙悟空在游戏中被封印500年后重新苏醒",
    "世界中存在佛教、道教等多个势力的神魔",
    "玩家扮演的孙悟空需要在各方势力中寻找真相",
    "游戏场景包括水墨画风格的山川和建筑"
]
# 创建两种不同的检索器：BM25+向量检索器
from langchain_community.retrievers import BM25Retriever # BM25检索器
from langchain_community.vectorstores import FAISS # 向量数据库，此时不是检索器
from langchain.retrievers import EnsembleRetriever # 混合检索器
# 创建BM25检索器
bm25_retriever = BM25Retriever.from_texts(
    system_docs + lore_docs,
    metadatas=[{"source": "system" if i < len(system_docs) else "lore"} 
               for i in range(len(system_docs) + len(lore_docs))]
)
bm25_retriever.k = 2
# 创建向量检索器
embed_model = HuggingFaceEmbeddings(model_name="BAAI/bge-small-zh")
vectorstore = FAISS.from_texts(
    system_docs + lore_docs,
    embed_model,
    metadatas=[{"source": "system" if i < len(system_docs) else "lore"} 
               for i in range(len(system_docs) + len(lore_docs))]
)
faiss_retriever = vectorstore.as_retriever(search_kwargs={"k": 2}) # 创建向量检索器，基于向量数据库
# 创建混合检索器
ensemble_retriever = EnsembleRetriever(
    retrievers=[bm25_retriever, faiss_retriever], # 混合检索器，包含两个检索器
    weights=[0.5, 0.5] # 权重，用于平衡两个检索器的贡献 -> weightedreranker
)
# 创建使用混合检索器的问答链和使用单一检索器的问答链（用于对比）
llm = ChatDeepSeek(model="deepseek-chat")
# 创建混合检索问答链 -> 有点像集成学习方法
ensemble_qa = RetrievalQA.from_chain_type(
    llm=llm,
    retriever=ensemble_retriever,
    return_source_documents=True
)
# 创建单独的向量检索问答链（用于对比）
vector_qa = RetrievalQA.from_chain_type(
    llm=llm,
    retriever=faiss_retriever,
    return_source_documents=True
)
# 测试不同类型的查询
test_queries = [
    "游戏中的变身系统是什么样的？",  # 系统机制查询
    "游戏的世界背景是怎样的？",      # 背景设定查询
    "悟空有哪些战斗形态？"           # 混合查询
]
for query in test_queries:
    print(f"\n查询：{query}")
    print("\n1. 混合检索结果：")
    ensemble_docs = ensemble_retriever.invoke(query)
    print("检索到的文档：")
    for i, doc in enumerate(ensemble_docs, 1):
        print(f"{i}. [{doc.metadata['source']}] {doc.page_content}")    
    print("\n2. 向量检索结果（对比）：")
    vector_docs = faiss_retriever.invoke(query)
    print("检索到的文档：")
    for i, doc in enumerate(vector_docs, 1):
        print(f"{i}. [{doc.metadata['source']}] {doc.page_content}")
#  测试问答效果
print("\n=== 问答效果测试 ===")
test_questions = [
    "金刚形态的特点是什么？",
    "游戏中的势力分布是怎样的？",
]
for question in test_questions:
    print(f"\n问题：{question}")    
    print("\n1. 使用混合检索的回答：")
    ensemble_result = ensemble_qa.invoke({"query": question})
    print(f"回答：{ensemble_result['result']}")
    print("\n使用的源文档：")
    for i, doc in enumerate(ensemble_result['source_documents'], 1):
        print(f"{i}. [{doc.metadata['source']}] {doc.page_content}")    
    print("\n2. 使用纯向量检索的回答（对比）：")
    vector_result = vector_qa.invoke({"query": question})
    print(f"回答：{vector_result['result']}")
    print("\n使用的源文档：")
    for i, doc in enumerate(vector_result['source_documents'], 1):
        print(f"{i}. [{doc.metadata['source']}] {doc.page_content}")
