import os
import openai
import pandas as pd
import numpy as np
import json
from sklearn.metrics.pairwise import cosine_similarity

# 读取用户评价数据集
df = pd.read_csv("90-文档-Data/灭神纪/用户评价.csv")

# 读取游戏描述文件
with open("90-文档-Data/灭神纪/游戏说明.json", "r") as f:
    game_descriptions = json.load(f)

# 定义函数获取嵌入向量
def get_embedding(text, model="text-embedding-3-small"):
    response = openai.embeddings.create(
        input=[text],
        model=model
    )
    return response.data[0].embedding

# 获取所有游戏的嵌入向量
unique_games = df['game_title'].unique().tolist()
target_game = "Killing God: Hu Sun"  # 目标游戏名称更改
if target_game not in unique_games:
    unique_games.append(target_game)  # 确保目标游戏在列表中
game_embeddings = {}
for game in unique_games:
    description = game_descriptions[game]
    game_embeddings[game] = np.array(get_embedding(description))

# 计算用户评价的嵌入向量（该用户评价过的所有游戏描述嵌入向量的平均值）
user_vectors = {}
for user_id, group in df.groupby("user_id"):
    user_game_vecs = []
    for idx, row in group.iterrows():
        g_title = row['game_title']
        g_vec = game_embeddings[g_title]
        user_game_vecs.append(g_vec)
    user_vectors[user_id] = np.mean(np.array(user_game_vecs), axis=0)

# 获取“灭神纪·猢狲”的嵌入向量
target_vector = game_embeddings[target_game]
# 计算每个用户评价的嵌入向量与目标游戏的嵌入向量的余弦相似度
results = []
for user_id, u_vec in user_vectors.items():
    u_vec_reshaped = u_vec.reshape(1, -1)
    t_vec = target_vector.reshape(1, -1)
    similarity = cosine_similarity(u_vec_reshaped, t_vec)[0,0]
    results.append((user_id, similarity))
    
# 排序并找出最可能喜欢“灭神纪·猢狲”的用户
result_df = pd.DataFrame(results, columns=["user_id", f"similarity_to_{target_game}"])
result_df = result_df.sort_values(by=f"similarity_to_{target_game}", ascending=False)
print(f"\n最可能喜欢{target_game}的前5位用户：")
print(result_df.head())
