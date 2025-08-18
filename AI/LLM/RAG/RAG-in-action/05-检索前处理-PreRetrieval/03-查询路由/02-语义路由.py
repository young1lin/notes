from langchain.utils.math import cosine_similarity
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import PromptTemplate
from langchain_core.runnables import RunnableLambda, RunnablePassthrough
from langchain_openai import ChatOpenAI, OpenAIEmbeddings

# 定义两个提示模板
combat_template = """你是一位精通黑悟空战斗技巧的专家。
你擅长以简洁易懂的方式回答关于黑悟空战斗的问题。
当你不知道问题的答案时，你会坦诚相告。

以下是一个问题：
{query}"""

story_template = """你是一位熟悉黑悟空故事情节的专家。
你擅长将复杂的情节分解并详细解释。
当你不知道问题的答案时，你会坦诚相告。

以下是一个问题：
{query}"""

# 初始化嵌入模型
embeddings = OpenAIEmbeddings()
prompt_templates = [combat_template, story_template]
prompt_embeddings = embeddings.embed_documents(prompt_templates)

# 定义路由函数
def prompt_router(input):
    # 对用户问题进行嵌入
    query_embedding = embeddings.embed_query(input["query"])
    # 计算相似度
    similarity = cosine_similarity([query_embedding], prompt_embeddings)[0]
    most_similar = prompt_templates[similarity.argmax()]
    # 选择最相似的提示模板
    print("使用战斗技巧模板" if most_similar == combat_template else "使用故事情节模板")
    return PromptTemplate.from_template(most_similar)

# 创建处理链
chain = (
    {"query": RunnablePassthrough()}
    | RunnableLambda(prompt_router)
    | ChatOpenAI()
    | StrOutputParser()
)

# 示例问题
print(chain.invoke("黑悟空是如何打败敌人的？"))
