"""
多模态图像检索系统：基于Visualized-BGE和Milvus实现
功能：对图像和文本进行多模态编码，并在图像数据库中检索相似内容
"""

# ==================== 1. 初始化编码器 ====================
import torch
from visual_bge.modeling import Visualized_BGE
from dataclasses import dataclass
from typing import List, Optional
import json
from tqdm import tqdm
import numpy as np
import cv2
from PIL import Image
from pymilvus import MilvusClient

class WukongEncoder:
    """多模态编码器：将图像和文本编码成向量"""
    def __init__(self, model_name: str, model_path: str):
        self.model = Visualized_BGE(model_name_bge=model_name, model_weight=model_path)
        self.model.eval()
    
    def encode_query(self, image_path: str, text: str) -> list[float]:
        """编码图像和文本的组合查询"""
        with torch.no_grad():
            query_emb = self.model.encode(image=image_path, text=text)
        return query_emb.tolist()[0]
    
    def encode_image(self, image_path: str) -> list[float]:
        """仅编码图像"""
        with torch.no_grad():
            query_emb = self.model.encode(image=image_path)
        return query_emb.tolist()[0]

# 初始化编码器
model_name = "BAAI/bge-base-en-v1.5"
model_path = "./Visualized_base_en_v1.5.pth"
encoder = WukongEncoder(model_name, model_path)

# ==================== 2. 数据集管理 ====================
@dataclass
class WukongImage:
    """图像元数据结构"""
    image_id: str
    file_path: str
    title: str
    category: str
    description: str
    tags: List[str]
    game_chapter: str
    location: str
    characters: List[str]
    abilities_shown: List[str]
    environment: str
    time_of_day: str

class WukongDataset:
    """图像数据集管理类"""
    def __init__(self, data_dir: str, metadata_path: str):
        self.data_dir = data_dir
        self.metadata_path = metadata_path
        self.images: List[WukongImage] = []
        self._load_metadata()
    
    def _load_metadata(self):
        """加载图像元数据"""
        with open(self.metadata_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
            for img_data in data['images']:
                # 确保图片路径是相对于 data_dir 的
                img_data['file_path'] = f"{self.data_dir}/{img_data['file_path'].split('/')[-1]}"
                self.images.append(WukongImage(**img_data))

# 初始化数据集
dataset = WukongDataset("/home/huangj2/Documents/rag-in-action/90-文档-Data/多模态", "/home/huangj2/Documents/rag-in-action/90-文档-Data/多模态/metadata.json")

# ==================== 3. 生成图像嵌入 ====================
# 为所有图像生成嵌入向量
image_dict = {}
for image in tqdm(dataset.images, desc="生成图片嵌入"):
    try:
        image_dict[image.file_path] = encoder.encode_image(image.file_path)
    except Exception as e:
        print(f"处理图片 {image.file_path} 失败：{str(e)}")
        continue

print(f"成功编码 {len(image_dict)} 张图片")

# ==================== 4. Milvus向量库设置 ====================
# 连接/创建Milvus数据库
collection_name = "wukong_scenes"
milvus_client = MilvusClient(uri="./wukong_images.db")

# 创建向量集合
dim = len(list(image_dict.values())[0])
milvus_client.create_collection(
    collection_name=collection_name,
    dimension=dim,
    auto_id=True,
    enable_dynamic_field=True
)

# 插入数据到Milvus
insert_data = []
for image in dataset.images:
    if image.file_path in image_dict:
        insert_data.append({
            "image_path": image.file_path,
            "vector": image_dict[image.file_path],
            "title": image.title,
            "category": image.category,
            "description": image.description,
            "tags": ",".join(image.tags),
            "game_chapter": image.game_chapter,
            "location": image.location,
            "characters": ",".join(image.characters),
            "abilities": ",".join(image.abilities_shown),
            "environment": image.environment,
            "time_of_day": image.time_of_day
        })

result = milvus_client.insert(
    collection_name=collection_name,
    data=insert_data
)
print(f"索引构建完成，共插入 {result['insert_count']} 条记录")

# ==================== 5. 搜索功能实现 ====================
def search_similar_images(
    query_image: str,
    query_text: str,
    limit: int = 9
) -> List[dict]:
    """
    搜索相似图像
    参数:
        query_image: 查询图像路径
        query_text: 查询文本
        limit: 返回结果数量
    返回:
        检索结果列表
    """
    # 生成查询向量
    query_vec = encoder.encode_query(query_image, query_text)
    
    # 构建搜索参数
    search_params = {
        "metric_type": "COSINE",
        "params": {
            "nprobe": 10,
            "radius": 0.1,
            "range_filter": 0.8
        }
    }

    # 执行搜索
    results = milvus_client.search(
        collection_name=collection_name,
        data=[query_vec],
        output_fields=[
            "image_path", "title", "category", "description",
            "tags", "game_chapter", "location", "characters",
            "abilities", "environment", "time_of_day"
        ],
        limit=limit,
        search_params=search_params
    )[0]
    
    return results

# ==================== 6. 可视化函数 ====================
def visualize_results(query_image: str, results: List[dict], output_path: str):
    """
    可视化搜索结果
    参数:
        query_image: 查询图像路径
        results: 搜索结果列表
        output_path: 输出图像路径
    """
    # 设置图片大小和网格参数
    img_size = (300, 300)
    grid_size = (3, 3)
    
    # 创建画布
    canvas_height = img_size[0] * (grid_size[0] + 1)
    canvas_width = img_size[1] * (grid_size[1] + 1)
    canvas = np.full((canvas_height, canvas_width, 3), 255, dtype=np.uint8)
    
    # 添加查询图片
    query_img = Image.open(query_image).convert("RGB")
    query_array = np.array(query_img)
    query_resized = cv2.resize(query_array, (img_size[0] - 20, img_size[1] - 20))
    bordered_query = cv2.copyMakeBorder(
        query_resized, 10, 10, 10, 10,
        cv2.BORDER_CONSTANT,
        value=(255, 0, 0)
    )
    canvas[:img_size[0], :img_size[1]] = bordered_query
    
    # 添加结果图片
    for idx, result in enumerate(results[:grid_size[0] * grid_size[1]]):
        row = (idx // grid_size[1]) + 1
        col = idx % grid_size[1]
        
        img = Image.open(result["entity"]["image_path"]).convert("RGB")
        img_array = np.array(img)
        resized = cv2.resize(img_array, (img_size[0], img_size[1]))
        
        y_start = row * img_size[0]
        x_start = col * img_size[1]
        
        canvas[y_start:y_start + img_size[0], x_start:x_start + img_size[1]] = resized
        
        # 添加相似度分数
        score_text = f"Score: {result['distance']:.2f}"
        cv2.putText(
            canvas,
            score_text,
            (x_start + 10, y_start + img_size[0] - 10),
            cv2.FONT_HERSHEY_SIMPLEX,
            0.5,
            (0, 0, 0),
            1
        )
    
    cv2.imwrite(output_path, canvas)

# ==================== 7. 执行查询示例 ====================
# 执行查询
query_image = "/home/huangj2/Documents/rag-in-action/90-文档-Data/多模态/query_image.jpg"
query_text = "寻找悟空面对建筑物战斗场景"

results = search_similar_images(
    query_image=query_image,
    query_text=query_text,
    limit=9
)

# 输出详细信息
print("\n搜索结果:")
for idx, result in enumerate(results):
    print(f"\n结果 {idx}:")
    print(f"图片：{result['entity']['image_path']}")
    print(f"标题：{result['entity']['title']}")
    print(f"描述：{result['entity']['description']}")
    print(f"相似度分数：{result['distance']:.4f}")

# 可视化结果
visualize_results(query_image, results, "search_results.jpg")
