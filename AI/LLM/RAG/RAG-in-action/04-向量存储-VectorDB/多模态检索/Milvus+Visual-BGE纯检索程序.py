"""
纯检索程序：基于已构建的Milvus向量库进行检索
"""

import torch
from pymilvus import MilvusClient
from PIL import Image
import cv2
import numpy as np
from typing import List, Optional, Dict
from visual_bge.modeling import Visualized_BGE

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

class MilvusSearcher:
    """Milvus检索器"""
    def __init__(self, db_path: str, collection_name: str):
        self.client = MilvusClient(uri=db_path)
        self.collection_name = collection_name
    
    def search(
        self,
        query_vector: List[float],
        limit: int = 9,
        filters: Optional[Dict] = None
    ) -> List[Dict]:
        """
        执行向量检索
        参数:
            query_vector: 查询向量
            limit: 返回结果数量
            filters: 过滤条件
        返回:
            检索结果列表
        """
        # 构建搜索参数
        search_params = {
            "metric_type": "COSINE",
            "params": {"nprobe": 10}
        }

        # 构建 filter 表达式
        filter_expr = None
        if filters:
            conds = []
            for k, v in filters.items():
                if isinstance(v, list):
                    vs = ", ".join(f"'{x}'" for x in v)
                    conds.append(f"{k} in [{vs}]")
                else:
                    conds.append(f"{k} == '{v}'")
            filter_expr = " and ".join(conds)

        # 执行搜索
        results = self.client.search(
            collection_name=self.collection_name,
            data=[query_vector],
            filter=filter_expr,
            limit=limit,
            output_fields=[
                "image_path", "title", "category", "description",
                "tags", "game_chapter", "location", "characters",
                "abilities", "environment", "time_of_day"
            ],
            search_params=search_params
        )[0]
        
        return results

def visualize_results(query_image: str, results: List[dict], output_path: str):
    """
    可视化搜索结果
    参数:
        query_image: 查询图像路径
        results: 搜索结果列表
        output_path: 输出图像路径
    """
    img_size = (300, 300)
    grid_size = (3, 3)
    
    canvas_h = img_size[1] * (grid_size[0] + 1)
    canvas_w = img_size[0] * (grid_size[1] + 1)
    canvas = np.full((canvas_h, canvas_w, 3), 255, dtype=np.uint8)
    
    # 放置查询图
    qimg = Image.open(query_image).convert("RGB")
    qarr = np.array(qimg)
    qrs = cv2.resize(qarr, (img_size[0]-20, img_size[1]-20))
    bq = cv2.copyMakeBorder(qrs, 10,10,10,10, cv2.BORDER_CONSTANT, value=(255,0,0))
    canvas[0:img_size[1], 0:img_size[0]] = bq
    
    # 放置结果图
    for i, r in enumerate(results[:grid_size[0]*grid_size[1]]):
        row = (i // grid_size[1]) + 1
        col = i % grid_size[1]
        img = Image.open(r["entity"]["image_path"]).convert("RGB")
        arr = np.array(img)
        rs = cv2.resize(arr, img_size)
        y0 = row * img_size[1]
        x0 = col * img_size[0]
        canvas[y0:y0+img_size[1], x0:x0+img_size[0]] = rs
        text = f"Score:{r['distance']:.2f}"
        cv2.putText(canvas, text, (x0+10, y0+img_size[1]-10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0,0,0), 1)
    
    cv2.imwrite(output_path, canvas)

def print_results(results: List[dict]):
    """打印搜索结果"""
    print("\n搜索结果:")
    for idx, r in enumerate(results):
        print(f"\n结果 {idx+1}:")
        print(f"  图片: {r['entity']['image_path']}")
        print(f"  标题: {r['entity']['title']}")
        print(f"  描述: {r['entity']['description']}")
        print(f"  环境: {r['entity']['environment']}")
        print(f"  类别: {r['entity']['category']}")
        print(f"  相似度: {r['distance']:.4f}")

if __name__ == "__main__":
    # 初始化编码器（根据需要换中文模型）
    model_name = "BAAI/bge-base-en-v1.5"
    model_path = "./Visualized_base_en_v1.5.pth"
    encoder = WukongEncoder(model_name, model_path)
    
    # 初始化检索器
    searcher = MilvusSearcher("./wukong_images.db", "wukong_scenes")
    
    # 生成查询向量
    query_image = "90-文档-Data/多模态/query_image.jpg"
    query_text = "寻找类似的雪地战斗场景"
    qvec = encoder.encode_query(query_image, query_text)
    
    # 带过滤条件检索
    filters = {"environment": "雪地", "category": "combat"}
    res_f = searcher.search(qvec, limit=9, filters=filters)
    print("\n带过滤结果:")
    print_results(res_f)
    visualize_results(query_image, res_f, "search_with_filter.jpg")
    
    # 不带过滤条件检索
    res_nf = searcher.search(qvec, limit=9)
    print("\n无过滤结果:")
    print_results(res_nf)
    visualize_results(query_image, res_nf, "search_without_filter.jpg")
