from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch

"""
CrossEncoder重排算法实现

CrossEncoder是一种基于BERT的双向编码器重排模型，专门用于计算查询-文档对的相关性分数。

核心原理：
1. 将查询和文档作为一个整体输入到BERT模型中
2. 利用[CLS]标记的输出来预测相关性分数
3. 通过端到端的方式训练，能够捕捉查询与文档之间的深层交互

与其他方法的区别：
- 相比于双塔模型(Bi-Encoder)：CrossEncoder能够更好地建模查询与文档间的交互
- 相比于传统BM25：能够理解语义相似性，不仅仅依赖关键词匹配
- 相比于简单的向量相似度：考虑了查询与文档的位置信息和上下文关系

优势：
- 精度高：能够精确建模查询-文档对的相关性
- 语义理解强：基于预训练语言模型，具备强大的语义理解能力
- 适应性好：可以通过微调适应特定领域

劣势：
- 计算开销大：需要对每个查询-文档对单独编码
- 实时性差：不适合大规模检索的第一阶段，通常用于重排阶段
"""

print("🔄 初始化CrossEncoder重排模型...")

# 1. 加载预训练的CrossEncoder模型
print("📥 加载预训练模型...")
model_name = "cross-encoder/ms-marco-MiniLM-L-12-v2"  # MS MARCO数据集上训练的小型模型
print(f"使用模型: {model_name}")
print("  - 该模型在MS MARCO段落检索任务上进行了微调")
print("  - 专门优化用于计算查询-段落相关性分数")
print("  - 平衡了模型大小和性能，适合生产环境使用")

tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForSequenceClassification.from_pretrained(model_name)
print("✅ 模型加载完成")

# 2. 准备测试数据
print("\n📋 准备测试数据...")
query = "山西有哪些著名的旅游景点？"
documents = [
    "五台山是中国四大佛教名山之一，以文殊菩萨道场闻名。",
    "云冈石窟是中国三大石窟之一，以精美的佛教雕塑著称。",
    "平遥古城是中国保存最完整的古代县城之一，被列为世界文化遗产。",
]

print(f"查询: {query}")
print(f"候选文档数量: {len(documents)}")
for i, doc in enumerate(documents, 1):
    print(f"  文档 {i}: {doc}")

def encode_and_score(query, docs):
    """
    CrossEncoder相关性评分函数
    
    功能：计算查询与每个文档的相关性分数
    
    参数：
        query (str): 用户查询
        docs (list): 候选文档列表
    
    返回：
        list: 每个文档对应的相关性分数
    
    工作流程：
        1. 将查询和文档拼接为"[CLS] query [SEP] document [SEP]"格式
        2. 通过tokenizer进行编码，生成input_ids、attention_mask等
        3. 输入到BERT模型中，获取[CLS]位置的输出
        4. 通过分类头计算相关性分数
        5. 分数越高表示相关性越强
    """
    print(f"\n🧠 开始计算 {len(docs)} 个文档的相关性分数...")
    scores = []
    
    for i, doc in enumerate(docs, 1):
        print(f"  处理文档 {i}/{len(docs)}...")
        
        # 将查询和文档组合为BERT输入格式
        # 格式: [CLS] query [SEP] document [SEP]
        inputs = tokenizer(
            query, 
            doc, 
            return_tensors="pt",           # 返回PyTorch张量
            truncation=True,               # 截断过长的输入
            max_length=512,                # BERT最大输入长度
            padding="max_length"           # 填充到最大长度
        )
        
        # 前向传播计算相关性分数
        with torch.no_grad():  # 禁用梯度计算，节省内存
            outputs = model(**inputs)
            # 获取logits（原始分数），通常第一个元素是相关性分数
            score = outputs.logits[0][0].item()
            scores.append(score)
            
        print(f"    查询-文档对相关性分数: {score:.4f}")
        print(f"    输入长度: {len(inputs['input_ids'][0])} tokens")
    
    print("✅ 相关性分数计算完成")
    return scores

# 3. 执行CrossEncoder重排
print(f"\n🎯 执行CrossEncoder重排...")
scores = encode_and_score(query, documents)

# 4. 根据分数排序文档
print(f"\n📊 根据相关性分数排序文档...")
ranked_docs = sorted(zip(documents, scores), key=lambda x: x[1], reverse=True)

# 5. 输出重排结果
print(f"\n{'='*60}")
print(f"🏆 CrossEncoder重排结果")
print(f"{'='*60}")
print(f"查询: {query}")
print(f"\n排序结果（按相关性分数降序）:")

for rank, (doc, score) in enumerate(ranked_docs, start=1):
    print(f"\n📄 排名 {rank}:")
    print(f"   相关性分数: {score:.4f}")
    print(f"   文档内容: {doc}")
    
    # 解释分数含义
    if score > 0:
        relevance_level = "高度相关"
    elif score > -2:
        relevance_level = "中等相关"
    else:
        relevance_level = "低相关"
    print(f"   相关性级别: {relevance_level}")

print(f"\n📋 CrossEncoder重排总结:")
print("- ✅ 深度语义理解：捕捉查询与文档间的细粒度交互")
print("- ✅ 精确相关性建模：端到端训练获得准确的相关性分数")
print("- ✅ 上下文感知：考虑词汇的位置信息和上下文关系")
print("- ⚠️  计算密集：每个查询-文档对都需要单独编码")
print("- 💡 最佳实践：用于对初检索结果进行精细重排")
