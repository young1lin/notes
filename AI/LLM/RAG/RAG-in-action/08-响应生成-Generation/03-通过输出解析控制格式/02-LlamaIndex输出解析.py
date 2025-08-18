from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from llama_index.core.response_synthesizers import get_response_synthesizer
from llama_index.core.response_synthesizers.type import ResponseMode
from llama_index.core.prompts import PromptTemplate
from pydantic import BaseModel, Field
from typing import List

# 定义游戏信息结构
class GameInfo(BaseModel):
    title: str = Field(description="游戏名称")
    developer: str = Field(description="开发商")
    release_date: str = Field(description="发行日期")
    platforms: List[str] = Field(description="支持平台")
    main_features: List[str] = Field(description="主要特点")
    story_summary: str = Field(description="故事概要")
    reception: str = Field(description="市场反响")

# 载入数据
documents = SimpleDirectoryReader(input_files=["90-文档-Data/黑悟空/黑悟空wiki.txt"], encoding="utf-8").load_data()
index = VectorStoreIndex.from_documents(documents)

# 1. 基础解析模式 - 使用COMPACT模式
print("=== 基础解析模式 ===")
synthesizer = get_response_synthesizer(
    response_mode=ResponseMode.COMPACT,
    verbose=True    # 显示详细信息
)
query_engine = index.as_query_engine(response_synthesizer=synthesizer)
response = query_engine.query("请总结《黑神话：悟空》这款游戏的主要内容")
print(response)

# 2. 结构化解析模式 - 使用REFINE模式
print("\n=== 结构化解析模式 ===")
synthesizer = get_response_synthesizer(
    response_mode=ResponseMode.REFINE,
    output_cls=GameInfo,  # 指定输出类
    verbose=True
)
query_engine = index.as_query_engine(response_synthesizer=synthesizer)
response = query_engine.query("请提取《黑神话：悟空》的关键信息")
# 安全地处理响应
if hasattr(response, 'response'):
    print(response.response)
else:
    print(response)

# 3. 表格格式解析 - 使用TREE_SUMMARIZE模式
print("\n=== 游戏特点表格解析 ===")
table_prompt = PromptTemplate(
    template="请将以下游戏特点以表格形式展示：\n{query_str}\n格式要求：\n| 类别 | 内容 |\n|------|------|\n"
)
synthesizer = get_response_synthesizer(
    response_mode=ResponseMode.TREE_SUMMARIZE,
    summary_template=table_prompt,
    verbose=True
)
query_engine = index.as_query_engine(response_synthesizer=synthesizer)
response = query_engine.query("请用表格形式总结《黑神话：悟空》的主要特点")
print(response)

# 4. 分点解析模式 - 使用COMPACT_ACCUMULATE模式
print("\n=== 游戏亮点分点解析 ===")
bullet_prompt = PromptTemplate(
    template="请将以下游戏亮点以分点形式展示：\n{query_str}\n格式要求：\n1. \n2. \n3. "
)
synthesizer = get_response_synthesizer(
    response_mode=ResponseMode.COMPACT_ACCUMULATE,
    text_qa_template=bullet_prompt,
    verbose=True,
    use_async=True  # 启用异步处理
)
query_engine = index.as_query_engine(response_synthesizer=synthesizer)
response = query_engine.query("请用分点形式总结《黑神话：悟空》的亮点")
print(response)

# 5. 故事线解析 - 使用SIMPLE_SUMMARIZE模式
print("\n=== 游戏故事线解析 ===")
story_prompt = PromptTemplate(
    template="请将以下游戏故事以时间线形式展示：\n{query_str}\n格式要求：\n- 时间点：事件\n"
)
synthesizer = get_response_synthesizer(
    response_mode=ResponseMode.SIMPLE_SUMMARIZE,
    text_qa_template=story_prompt,
    verbose=True
)
query_engine = index.as_query_engine(response_synthesizer=synthesizer)
response = query_engine.query("请用时间线形式总结《黑神话：悟空》的故事发展")
print(response)
