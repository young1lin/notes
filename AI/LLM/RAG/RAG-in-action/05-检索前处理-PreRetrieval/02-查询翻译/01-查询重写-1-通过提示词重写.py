from openai import OpenAI
from os import getenv
# 初始化OpenAI客户端，指定DeepSeek URL
client = OpenAI(
    base_url="https://api.deepseek.com",
    api_key=getenv("DEEPSEEK_API_KEY")
)
def rewrite_query(question: str) -> str:
    """使用大模型重写查询"""
    prompt = """作为一个游戏客服人员，你需要帮助用户重写他们的问题。

规则：
1. 移除无关信息（如个人情况、闲聊内容）
2. 使用精确的游戏术语表达
3. 保持问题的核心意图
4. 将模糊的问题转换为具体的查询
原始问题：{question}
请直接给出重写后的查询（不要加任何前缀或说明）。"""
    # 使用DeepSeek模型重写查询   
    response = client.chat.completions.create(
        model="deepseek-chat",
        messages=[
            {"role": "user", "content": prompt.format(question=question)}
        ],
        temperature=0
    )
    return response.choices[0].message.content.strip()
# 开始测试
query = "那个，我刚开始玩这个游戏，感觉很难，在普陀山那一关，嗯，怎么也过不去。先学什么技能比较好？新手求指导！"
print(f"\n原始查询：{query}")
print(f"重写查询：{rewrite_query(query)}")
