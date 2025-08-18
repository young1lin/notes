from FlagEmbedding import BGEM3FlagModel

def main():
    model = BGEM3FlagModel("BAAI/bge-m3", use_fp16=False)
    passage = ["猢狲施展烈焰拳，击退妖怪；随后开启金刚体，抵挡神兵攻击。"]

    # 编码文本，获取稀疏嵌入和密集嵌入
    passage_embeddings = model.encode(
        passage,
        return_sparse=True,     # 返回稀疏嵌入
        return_dense=True,      # 返回密集嵌入
        return_colbert_vecs=True  # 返回多向量嵌入
    )
    # 分别提取稀疏嵌入、密集嵌入和多向量嵌入
    dense_vecs = passage_embeddings["dense_vecs"]
    sparse_vecs = passage_embeddings["lexical_weights"]
    colbert_vecs = passage_embeddings["colbert_vecs"]
    # 展示稀疏嵌入和密集嵌入的示例
    print("密集嵌入维度:", dense_vecs[0].shape)
    print("密集嵌入前10维:", dense_vecs[0][:10])  # 仅显示前10维
    
    print("稀疏嵌入总长度:", len(sparse_vecs[0]))
    print("稀疏嵌入前10个非零值:", list(sparse_vecs[0].items())[:10])  # 仅显示前10个非零值
    
    print("多向量嵌入维度:", colbert_vecs[0].shape) 
    print("多向量嵌入前2个:", colbert_vecs[0][:2])  # 仅显示前2个多向量嵌入

if __name__ == '__main__':
    main()
