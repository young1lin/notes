# 课后作业：同学们可以基于这个代码框架，尝试用多模态RAG来生成一个图片
# 不仅实现多模态检索，还可以还进一步基于检索内容，组合所有信息，利用现代LLM生成新的文本或图像。

import weaviate
import weaviate.classes as wvc
import requests
from openai import OpenAI

# 1. 连接Weaviate实例（本地或云端）
client = weaviate.connect_to_local()  # 如用云服务请替换为 connect_to_wcs/wcs_cloud

# 2. 创建多模态集合（Collection）
def create_multimodal_collection():
    client.collections.create(
        name="Animals",
        vectorizer_config=wvc.config.Configure.Vectorizer.multi2vec_bind(
            audio_fields=["audio"],
            image_fields=["image"],
            video_fields=["video"],
        )
    )
    print("多模态集合 'Animals' 创建完成")

# 3. 插入多模态数据（以图片为例）
def insert_multimodal_data():
    animals = client.collections.get("Animals")
    # 这里假设有一张图片的base64字符串
    image_base64 = "<你的图片base64字符串>"
    animals.data.insert({
        "name": "puppy",
        "image": image_base64,
        "mediaType": "image"
    })
    print("图片数据已插入")

# 4. 检索图片（以文本为query）
def retrieve_image(query):
    animals = client.collections.get("Animals")
    response = animals.query.near_text(
        query=query,
        filters=wvc.query.Filter(path="mediaType").equal("image"),
        return_properties=['name','mediaType','image'],
        limit=1,
    )
    result = response.objects[0].properties
    print("检索到的图片对象:", result)
    return result['image']

# 5. 用GPT-4V生成图片描述
def generate_description_from_image_gpt4(prompt, image64, openai_api_key):
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {openai_api_key}"
    }
    payload = {
        "model": "gpt-4-vision-preview",
        "messages": [
            {
                "role": "user",
                "content": [
                    {"type": "text", "text": prompt},
                    {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{image64}"}}
                ]
            }
        ],
        "max_tokens": 300
    }
    response_oai = requests.post("https://api.openai.com/v1/chat/completions", headers=headers, json=payload)
    result = response_oai.json()['choices'][0]['message']['content']
    print(f"生成的描述: {result}")
    return result

# 6. 用DALL-E-3根据描述生成图片
def generate_image_dalee3(prompt, openai_api_key):
    openai_client = OpenAI(api_key=openai_api_key)
    response_oai = openai_client.images.generate(
        model="dall-e-3",
        prompt=str(prompt),
        size="1024x1024",
        quality="standard",
        n=1,
    )
    result = response_oai.data[0].url
    print(f"生成的图片URL: {result}")
    return result

if __name__ == "__main__":
    # 步骤演示
    # 1. 创建集合
    create_multimodal_collection()
    # 2. 插入数据（请先替换图片base64字符串）
    insert_multimodal_data()
    # 3. 检索图片
    image64 = retrieve_image("dog with a sign")
    # 4. 用GPT-4V生成描述（请替换为你的OpenAI API Key）
    description = generate_description_from_image_gpt4(
        prompt="这是一张我的宠物的图片，请给出可爱生动的描述。",
        image64=image64,
        openai_api_key="<你的OpenAI API Key>"
    )
    # 5. 用DALL-E-3生成图片
    generate_image_dalee3(description, openai_api_key="<你的OpenAI API Key>")
