from transformers import AutoTokenizer, AutoModel
import torch

"""
ColBERT（Contextualized Late Interaction over BERT）重排算法实现

ColBERT是一种结合了BERT深度语义理解和高效检索的创新架构，采用"后期交互"机制。

核心创新：
1. 早期分离编码：查询和文档分别独立编码，可以预先计算文档嵌入
2. 后期精细交互：在向量空间中进行细粒度的token级别交互
3. MaxSim操作：对于查询中的每个token，找到文档中最相似的token进行匹配

技术优势：
- 效率高：文档可以预先编码和索引，查询时只需编码查询
- 精度高：保留了token级别的精细交互，不损失语义信息
- 可扩展：支持大规模文档集合的高效检索

与其他方法对比：
- vs CrossEncoder：速度更快，支持预计算文档嵌入
- vs Bi-Encoder：交互更精细，考虑token级别的匹配
- vs 传统方法：语义理解能力更强，支持模糊匹配

适用场景：
- 大规模文档检索的第一阶段
- 需要平衡精度和效率的应用
- 对延迟敏感的实时检索系统
"""

print("🔄 初始化ColBERT重排模型...")

# 1. 加载BERT模型和分词器
print("📥 加载BERT基础模型...")
model_name = "bert-base-uncased"  # 基础BERT模型，可替换为ColBERT专门微调的模型
print(f"使用模型: {model_name}")
print("  注意: 在实际应用中，建议使用专门为ColBERT微调的模型")
print("  例如: 'colbert-ir/colbertv2.0' 或其他ColBERT优化模型")

tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModel.from_pretrained(model_name)
print("✅ 模型加载完成")

# 2. 准备测试数据
print("\n📋 准备测试数据...")
query = "山西有哪些著名的旅游景点？"
documents = [
    "五台山是中国四大佛教名山之一，以文殊菩萨道场闻名。",
    "云冈石窟是中国三大石窟之一，以精美的佛教雕塑著称。", 
    "平遥古城是中国保存最完整的古代县城之一，被列为世界文化遗产。"
]

print(f"查询: {query}")
print(f"候选文档数量: {len(documents)}")
for i, doc in enumerate(documents, 1):
    print(f"  文档 {i}: {doc}")

def encode_text(texts, max_length=128):
    """
    ColBERT文本编码函数
    
    功能：将文本编码为上下文化的token嵌入
    
    参数：
        texts (list): 要编码的文本列表
        max_length (int): 最大序列长度
    
    返回：
        torch.Tensor: shape为[batch_size, seq_len, hidden_size]的嵌入张量
    
    ColBERT编码特点：
        1. 保留所有token的独立嵌入（不仅仅是[CLS]）
        2. 每个token都有完整的上下文信息
        3. 为后期的token级别交互做准备
    """
    print(f"  🔤 编码文本，序列最大长度: {max_length}")
    
    inputs = tokenizer(
        texts,
        return_tensors="pt",      # 返回PyTorch张量
        padding=True,             # 填充到统一长度
        truncation=True,          # 截断过长序列
        max_length=max_length
    )
    
    print(f"    输入shape: {inputs['input_ids'].shape}")
    
    with torch.no_grad():
        outputs = model(**inputs)
    
    # 返回所有token的隐藏状态（不仅仅是[CLS]）
    embeddings = outputs.last_hidden_state
    print(f"    输出嵌入shape: {embeddings.shape}")
    
    return embeddings

print(f"\n🧠 开始ColBERT编码过程...")

# 3. 分别编码查询和文档
print(f"\n1️⃣ 编码查询...")
query_embeddings = encode_text([query])  # [1, seq_len, hidden_size]

print(f"\n2️⃣ 编码文档...")
doc_embeddings = encode_text(documents)  # [num_docs, seq_len, hidden_size]

def calculate_similarity(query_emb, doc_embs):
    """
    ColBERT相似度计算函数（简化版）
    
    功能：计算查询与文档之间的ColBERT相似度分数
    
    参数：
        query_emb (torch.Tensor): 查询嵌入 [1, seq_len, hidden_size]
        doc_embs (torch.Tensor): 文档嵌入 [num_docs, seq_len, hidden_size]
    
    返回：
        list: 每个文档的相似度分数
    
    ColBERT相似度计算步骤：
        1. 对每个查询token，找到与所有文档token的最大相似度（MaxSim）
        2. 将查询中所有token的MaxSim分数求和作为最终分数
        
    注意：这里使用简化版本（平均池化），完整的ColBERT使用MaxSim操作
    """
    print(f"\n3️⃣ 计算ColBERT相似度...")
    print("  注意: 这是ColBERT的简化实现，完整版本使用MaxSim操作")
    
    # 简化版本：使用平均池化代替完整的token级别交互
    # 在实际ColBERT中，这里会进行精细的MaxSim计算
    query_emb_pooled = query_emb.mean(dim=1)  # [1, hidden_size]
    doc_embs_pooled = doc_embs.mean(dim=1)    # [num_docs, hidden_size]
    
    print(f"    查询池化后shape: {query_emb_pooled.shape}")
    print(f"    文档池化后shape: {doc_embs_pooled.shape}")
    
    # L2归一化，确保计算余弦相似度
    query_emb_norm = query_emb_pooled / query_emb_pooled.norm(dim=1, keepdim=True)
    doc_embs_norm = doc_embs_pooled / doc_embs_pooled.norm(dim=1, keepdim=True)
    
    # 计算余弦相似度
    scores = torch.mm(query_emb_norm, doc_embs_norm.t())  # [1, num_docs]
    
    print(f"    相似度分数shape: {scores.shape}")
    
    return scores.squeeze().tolist()

# 4. 计算相似度分数
scores = calculate_similarity(query_embeddings, doc_embeddings)

# 5. 排序文档
print(f"\n📊 根据ColBERT相似度分数排序文档...")
ranked_docs = sorted(zip(documents, scores), key=lambda x: x[1], reverse=True)

# 6. 输出排序结果
print(f"\n{'='*60}")
print(f"🏆 ColBERT重排结果")
print(f"{'='*60}")
print(f"查询: {query}")
print(f"\n排序结果（按相似度分数降序）:")

for rank, (doc, score) in enumerate(ranked_docs, start=1):
    print(f"\n📄 排名 {rank}:")
    print(f"   ColBERT相似度分数: {score:.4f}")
    print(f"   文档内容: {doc}")
    
    # 解释分数含义
    if score > 0.8:
        relevance_level = "高度相关"
    elif score > 0.6:
        relevance_level = "中等相关"
    else:
        relevance_level = "低相关"
    print(f"   相关性级别: {relevance_level}")

print(f"\n📋 ColBERT算法总结:")
print("- ✅ 高效检索：支持文档预编码，查询时延迟低")
print("- ✅ 精细交互：保留token级别的语义交互信息")
print("- ✅ 可扩展性：适合大规模文档集合的检索")
print("- ✅ 平衡性能：在精度和效率之间取得良好平衡")
print("- 💡 完整实现：建议使用专门微调的ColBERT模型")
print("- 🔧 优化建议：实际应用中使用MaxSim操作替代简化的平均池化")

