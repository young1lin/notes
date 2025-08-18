from llama_index.core import Document

# 创建多个文档对象，并为其添加元数据
documents = [
    Document(
        text="一个充满烈焰和硫磺气息的地下洞窟，火焰从地底不断喷涌，照亮整个深渊。场景中有熔岩河流穿行，燃烧的火山石在空中漂浮。悟空需要利用自己的跳跃能力和金箍棒在熔岩之间穿行，同时对抗来自地狱的火焰妖怪。",
        metadata={
            "filename": "火照深渊场景.md",
            "category": "游戏场景",
            "file_path": "/data/黑悟空/火照深渊场景.md",
            "author": "GameScience",
            "creation_date": "2024-11-20",
            "last_modified_date": "2024-11-21",
            "file_type": "markdown",
            "word_count": 28,
        },
    ),
    Document(
        text="一片高耸入云的山脉，云雾缭绕，风力强劲。悟空需要通过飞跃山崖、利用筋斗云飞行，以及在大风中保持平衡来穿越场景。敌人主要是隐匿在云层中的飞禽妖怪和岩石机关兽。",
        metadata={
            "filename": "风起长空场景.md",
            "category": "游戏场景",
            "file_path": "/data/黑悟空/风起长空场景.md",
            "author": "GameScience",
            "creation_date": "2024-11-20",
            "last_modified_date": "2024-11-21",
            "file_type": "markdown",
            "word_count": 28,
        },
    )]   

# 打印每个文档的元数据
for doc in documents:
    print(f"Metadata for {doc.metadata['filename']}:")
    for key, value in doc.metadata.items():
        print(f"  {key}: {value}")
print("-" * 40)

