import logging
from typing import List
from langchain_chroma import Chroma
from langchain_community.document_loaders import TextLoader
from langchain_deepseek import ChatDeepSeek
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain.retrievers.multi_query import MultiQueryRetriever
from langchain_core.output_parsers import BaseOutputParser
from langchain.prompts import PromptTemplate
# 设置日志记录
logging.basicConfig()
logging.getLogger("langchain.retrievers.multi_query").setLevel(logging.INFO)
# 加载游戏相关文档并构建向量数据库
loader = TextLoader("90-文档-Data/黑悟空/黑悟空设定.txt", encoding='utf-8')
data = loader.load()
text_splitter = RecursiveCharacterTextSplitter(chunk_size=500, chunk_overlap=0)
splits = text_splitter.split_documents(data)
embed_model = HuggingFaceEmbeddings(model_name="BAAI/bge-small-zh")
vectorstore = Chroma.from_documents(documents=splits, embedding= embed_model)
# 自定义输出解析器
class LineListOutputParser(BaseOutputParser[List[str]]):
    def parse(self, text: str) -> List[str]:
        lines = text.strip().split("\n")
        return list(filter(None, lines))  # 过滤空行
output_parser = LineListOutputParser()
# 自定义查询提示模板
QUERY_PROMPT = PromptTemplate(
    input_variables=["question"],
    template="""你是一个资深的游戏客服。请从5个不同的角度重写用户的查询，以帮助玩家获得更详细的游戏指导。
                请确保每个查询都关注不同的方面，如技能选择、战斗策略、装备搭配等。
                用户原始问题：{question}
                请给出5个不同的查询，每个占一行。""",
)
# 设定大模型处理管道
llm = ChatDeepSeek(model="deepseek-chat", temperature=0)
llm_chain = QUERY_PROMPT | llm | output_parser
# 使用自定义提示模板的MultiQueryRetriever
retriever = MultiQueryRetriever(
    retriever=vectorstore.as_retriever(), 
    llm_chain=llm_chain, 
    parser_key="lines"
)
# 进行多角度查询
query = "那个，我刚开始玩这个游戏，感觉很难，请问这个游戏难度级别如何，有几关，在普陀山那一关，嗯，怎么也过不去。先学什么技能比较好？新手求指导！"
# 调用RePhraseQueryRetriever进行查询分解
docs = retriever.invoke(query)
print(docs)