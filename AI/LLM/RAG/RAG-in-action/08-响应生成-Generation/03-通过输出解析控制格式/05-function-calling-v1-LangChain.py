from langchain_deepseek import ChatDeepSeek 
from pydantic import BaseModel, Field

# 定义工具模式
class get_weather(BaseModel):
    """获取天气信息"""
    location: str = Field(..., description="城市名称")
    temperature: float = Field(..., description="温度")

# 初始化大模型
llm = ChatDeepSeek(model="deepseek-chat")

# 绑定工具
llm_with_tools = llm.bind_tools([get_weather])

# 发送请求
response = llm_with_tools.invoke("请告诉我上海的天气")

# 解析输出
if response.tool_calls:
    for tool_call in response.tool_calls:
        print(f"工具名称: {tool_call['name']}")
        print(f"参数: {tool_call['args']}")
else:
    print("没有工具调用")
