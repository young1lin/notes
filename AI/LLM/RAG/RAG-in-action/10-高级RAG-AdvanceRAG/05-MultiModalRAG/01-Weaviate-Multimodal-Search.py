import weaviate
import os
import base64
from weaviate.classes.config import Configure
from weaviate.classes.query import NearMediaType
# from IPython.display import Image, Audio, Video

# 连接到本地 Weaviate 实例
client = weaviate.connect_to_local()

# 检查并创建名为 "Monkey" 的集合
if client.collections.exists("Monkey"):
    client.collections.delete("Monkey")

client.collections.create(
    name="Monkey",
    vectorizer_config=Configure.Vectorizer.multi2vec_bind(
        image_fields=["image"],
        audio_fields=["audio"],
        video_fields=["video"]
    )
)

# 将文件转换为 base64 编码
def to_base64(path):
    with open(path, "rb") as f:
        return base64.b64encode(f.read()).decode("utf-8")

# 插入图像数据
image_dir = "90-文档-Data/多模态/Weaviate"
image_files = os.listdir(image_dir)
monkey = client.collections.get("Monkey")
for name in image_files:
    path = os.path.join(image_dir, name)
    monkey.data.insert({
        "name": name,
        "path": path,
        "image": to_base64(path),
        "mediaType": "image"
    })

# # 插入音频数据
# audio_dir = "./data/audio/"
# audio_files = os.listdir(audio_dir)
# for name in audio_files:
#     path = os.path.join(audio_dir, name)
#     animals.data.insert({
#         "name": name,
#         "path": path,
#         "audio": to_base64(path),
#         "mediaType": "audio"
#     })

# # 插入视频数据
# video_dir = "./data/video/"
# video_files = os.listdir(video_dir)
# for name in video_files:
#     path = os.path.join(video_dir, name)
#     animals.data.insert({
#         "name": name,
#         "path": path,
#         "video": to_base64(path),
#         "mediaType": "video"
#     })

# 文本搜索示例
query = "Monkey with fire"
response = monkey.query.near_text(
    query=query,
    return_properties=["name", "path", "mediaType"],
    limit=3
)
print(f"与查询词 '{query}' 相似的对象有：")
for obj in response.objects:
    print(obj.properties)

query = "Monsters"
response = monkey.query.near_text(
    query=query,
    return_properties=["name", "path", "mediaType"],
    limit=3
)
print(f"与查询词 '{query}' 相似的对象有：")
for obj in response.objects:
    print(obj.properties)

# 图像搜索示例
test_image_path = "90-文档-Data/多模态/query_image.jpg"
response = monkey.query.near_image(
    near_image=to_base64(test_image_path),
    return_properties=["name", "path", "mediaType"],
    limit=3
)
print("与当前图像相似的对象有：")
for obj in response.objects:
    print(obj.properties)

# # 音频搜索示例
# test_audio_path = "./test/test-audio.wav"
# response = animals.query.near_media(
#     media=to_base64(test_audio_path),
#     media_type=NearMediaType.AUDIO,
#     return_properties=["name", "path", "mediaType"],
#     limit=3
# )
# for obj in response.objects:
#     print(obj.properties)

# # 视频搜索示例
# test_video_path = "./test/test-video.mp4"
# response = animals.query.near_media(
#     media=to_base64(test_video_path),
#     media_type=NearMediaType.VIDEO,
#     return_properties=["name", "path", "mediaType"],
#     limit=3
# )
# for obj in response.objects:
#     print(obj.properties)

# img_path = "90-文档-Data/多模态/Weaviate/02.jpg"
# with open(img_path, "rb") as f:
#     image_base64 = base64.b64encode(f.read()).decode("utf-8")

# animals.data.insert({
#     "name": "02.jpg",
#     "image": image_base64,
#     "mediaType": "image"
# })


client.close()