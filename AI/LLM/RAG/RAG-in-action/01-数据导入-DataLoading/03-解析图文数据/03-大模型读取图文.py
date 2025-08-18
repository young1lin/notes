from pdf2image import convert_from_path
import base64
import os
from openai import OpenAI

# 初始化 OpenAI 客户端
client = OpenAI()
output_dir = "temp_images"

# 1. PDF 转图片
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

images = convert_from_path("90-文档-Data/黑悟空/黑神话悟空.pdf")
image_paths = []
for i, image in enumerate(images):
    image_path = os.path.join(output_dir, f'page_{i+1}.jpg')
    image.save(image_path, 'JPEG')
    image_paths.append(image_path)
print(f"成功转换 {len(image_paths)} 页")


# 2. GPT-4o 分析图片
print("\n开始分析图片...")
results = []
for image_path in image_paths:
    with open(image_path, "rb") as image_file:
        base64_image = base64.b64encode(image_file.read()).decode('utf-8')
    
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {
                "role": "user",
                "content": [
                    {"type": "text", "text": "请详细描述这张PPT幻灯片的内容，包括标题、正文和图片内容。"},
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:image/jpeg;base64,{base64_image}"
                        }
                    }
                ]
            }
        ],
        max_tokens=300
    )
results.append(response.choices[0].message.content)


# 3. 转换为 LangChain 的 Document 数据结构
from langchain_core.documents import Document

documents = [
    Document(
        page_content=result,
        metadata={"source": "data/黑悟空/黑神话悟空.pdf", "page_number": i + 1}
    )
    for i, result in enumerate(results)
]

# 输出所有生成的 Document 对象
print("\n分析结果：")
for doc in documents:
    print(f"内容: {doc.page_content}\n元数据: {doc.metadata}\n")
    print("-" * 80)

# 清理临时文件
for image_path in image_paths:
    os.remove(image_path)
os.rmdir(output_dir)

