# 导入所需的库
from langchain_core.prompts import ChatPromptTemplate
from langchain_deepseek import ChatDeepSeek 
from langchain_community.document_loaders import YoutubeLoader
from langchain.chains.query_constructor.base import AttributeInfo
from langchain.retrievers.self_query.base import SelfQueryRetriever
from langchain_chroma import Chroma
from langchain_huggingface import HuggingFaceEmbeddings
from pydantic import BaseModel, Field
# 定义视频元数据模型
class VideoMetadata(BaseModel):
    """视频元数据模型，定义了需要提取的视频属性"""
    source: str = Field(description="视频ID")
    title: str = Field(description="视频标题")
    description: str = Field(description="视频描述")
    view_count: int = Field(description="观看次数")
    publish_date: str = Field(description="发布日期")
    length: int = Field(description="视频长度(秒)")
    author: str = Field(description="作者")
# 加载视频数据
video_urls = [
    "https://www.youtube.com/watch?v=zDvnAY0zH7U",  # 山西佛光寺
    "https://www.youtube.com/watch?v=iAinNeOp6Hk",  # 中国最大宅院
    "https://www.youtube.com/watch?v=gCVy6NQtk2U",  # 宋代地下宫殿
]
# 加载视频元数据
videos = []
for url in video_urls:
    try:
        loader = YoutubeLoader.from_youtube_url(url, add_video_info=True)
        docs = loader.load()
        doc = docs[0]
        videos.append(doc)
        print(f"已加载：{doc.metadata['title']}")
    except Exception as e:
        print(f"加载失败 {url}: {str(e)}")
# 创建向量存储
embed_model = HuggingFaceEmbeddings(model_name="BAAI/bge-small-zh")
vectorstore = Chroma.from_documents(videos, embed_model)
# 配置检索器的元数据字段
metadata_field_info = [
    AttributeInfo(
        name="title",
        description="视频标题（字符串）",
        type="string", 
    ),
    AttributeInfo(
        name="author",
        description="视频作者（字符串）",
        type="string",
    ),
    AttributeInfo(
        name="view_count",
        description="视频观看次数（整数）",
        type="integer",
    ),
    AttributeInfo(
        name="publish_date",
        description="视频发布日期，格式为YYYY-MM-DD的字符串",
        type="string",
    ),
    AttributeInfo(
        name="length",
        description="视频长度，以秒为单位的整数",
        type="integer"
    ),
]
# 创建自查询检索器SelfQueryRetriever
llm = ChatDeepSeek(model="deepseek-chat", temperature=0)  # 确定性输出
retriever = SelfQueryRetriever.from_llm(
    llm=llm,
    vectorstore=vectorstore,
    document_contents="包含视频标题、作者、观看次数、发布日期等信息的视频元数据",
    metadata_field_info=metadata_field_info,
    enable_limit=True,
    verbose=True
)
# 执行示例查询
queries = [
    "找出观看次数超过100000的视频",
    "显示最新发布的视频"
]
# 执行查询并输出结果
for query in queries:
    print(f"\n查询：{query}")
    try:
        results = retriever.invoke(query)
        if not results:
            print("未找到匹配的视频")
            continue            
        for doc in results:
            print(f"标题：{doc.metadata['title']}")
            print(f"观看次数：{doc.metadata['view_count']}")
            print(f"发布日期：{doc.metadata['publish_date']}")
    except Exception as e:
        print(f"查询出错：{str(e)}")
        continue
