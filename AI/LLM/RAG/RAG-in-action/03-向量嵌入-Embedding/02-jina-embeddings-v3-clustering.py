import pandas as pd
import numpy as np
import requests
from sklearn.cluster import KMeans

# 1. 配置Jina API
url = 'https://api.jina.ai/v1/embeddings'
headers = {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer jina_4a0adace937d43299b955eb9146386a54B2Ubzak2NcPXcTekETSbKeDLtep'
}

# 2. 读取游戏描述数据
df = pd.read_csv("90-文档-Data/灭神纪/游戏描述.csv")
texts = df['description'].tolist()

# 3. 获取文本嵌入
data = {
    "model": "jina-embeddings-v3",
    "task": "text-matching",
    "dimensions": 1024,
    "normalized": True,
    "input": texts
}

response = requests.post(url, headers=headers, json=data)

if response.status_code != 200:
    raise RuntimeError(f"API调用失败: {response.status_code} - {response.text}")

embeddings = [item['embedding'] for item in response.json().get('data', [])]
if not embeddings:
    raise RuntimeError("API未返回嵌入向量")

embeddings = np.array(embeddings)

# 4. 聚类分析
kmeans = KMeans(n_clusters=3, random_state=42)
labels = kmeans.fit_predict(embeddings)

# 5. 打印结果
print("\n聚类结果：")
for i, lbl in enumerate(labels):
    print(f"Cluster {lbl}: {texts[i]}")
