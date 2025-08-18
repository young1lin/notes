from langchain_core.output_parsers import JsonOutputParser
from langchain_deepseek import ChatDeepSeek
from langchain.prompts import PromptTemplate
# 定义输出格式
parser = JsonOutputParser()
prompt = PromptTemplate.from_template("请返回JSON格式的用户信息：{query}")
# 调用大模型并解析
llm = ChatDeepSeek(model="deepseek-chat")
output = llm(prompt.format(query="用户ID 123"))
# 从 AIMessage 中提取内容
parsed_output = parser.parse(output.content)
print(parsed_output)
