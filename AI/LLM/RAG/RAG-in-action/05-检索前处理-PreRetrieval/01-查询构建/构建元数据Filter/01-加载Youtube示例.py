from langchain_community.document_loaders import YoutubeLoader

# 加载包含元数据的文档
docs = YoutubeLoader.from_youtube_url(
    "https://www.youtube.com/watch?v=zDvnAY0zH7U", add_video_info=True
).load()

# 查看加载的第一个文档的元数据
print(docs[0].metadata)
