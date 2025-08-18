"""
多模态嵌入简单示例：使用Visualized-BGE模型对图片进行编码
安装visual_bge，参考：
https://github.com/FlagOpen/FlagEmbedding/tree/master/research/visual_bge#readme

# 如果下载安装visual_bge后，依然无法使用，请切换到物理环境使用，虚拟环境可能无法使用
"""

import torch
from visual_bge.modeling import Visualized_BGE
from PIL import Image
import numpy as np

# 初始化编码器
model_name = "BAAI/bge-base-en-v1.5"
# 定义模型路径（如果有问题，请使用绝对路径）
# 请提前下载模型权重文件
# wget https://huggingface.co/BAAI/bge-visualized/resolve/main/Visualized_base_en_v1.5.pth
model_path = "/root/AI-BOX/code/rag/rag-in-action/03-向量嵌入-Embedding/Visualized_base_en_v1.5.pth"
model = Visualized_BGE(model_name_bge=model_name, model_weight=model_path)
model.eval()

# 定义图片路径（如果有问题，请使用绝对路径）
image_path = "/root/AI-BOX/code/rag/rag-in-action/90-文档-Data/多模态/query_image.jpg"

# 对图片进行编码
with torch.no_grad():
    # 仅使用图片进行编码
    image_embedding = model.encode(image=image_path)
    
    # 使用图片和文本进行编码
    text = "这是一张悟空战斗示例图片"
    multimodal_embedding = model.encode(image=image_path, text=text)

# 将张量转移到CPU并转换为numpy数组
image_embedding_np = image_embedding.cpu().numpy()
multimodal_embedding_np = multimodal_embedding.cpu().numpy()

# 打印嵌入向量的信息
print("=== 图片嵌入向量信息 ===")
print(f"向量维度: {image_embedding_np.shape[1]}")
print(f"向量示例 (前10个元素): {image_embedding_np[0][:10]}")
print(f"向量范数: {np.linalg.norm(image_embedding_np[0])}")

print("\n=== 多模态嵌入向量信息 ===")
print(f"向量维度: {multimodal_embedding_np.shape[1]}")
print(f"向量示例 (前10个元素): {multimodal_embedding_np[0][:10]}")
print(f"向量范数: {np.linalg.norm(multimodal_embedding_np[0])}") 