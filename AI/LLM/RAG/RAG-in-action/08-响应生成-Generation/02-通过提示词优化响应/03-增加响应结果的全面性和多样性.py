from openai import OpenAI
from dotenv import load_dotenv
import os
load_dotenv()

def get_code_snippet() -> str:
    """
    获取需要分析的代码片段。    
    返回:
        str: 包含代码片段的字符串
    """
    return """
            def handle_request(request):
                # 检查请求头中是否包含token
                if 'token' not in request.headers:
                    return {'status': 401, 'message': 'Unauthorized'}, 401
                
                try:
                    # 检查用户权限
                    check_permission(request.headers['token'])
                    
                    # 处理请求逻辑
                    return process_request(request)
                    
                except AccessDenied:
                    return {'status': 403, 'message': 'Forbidden'}, 403
                except Exception as e:
                        return {'status': 500, 'message': str(e)}, 500
            """

client = OpenAI(base_url="https://api.deepseek.com",
                api_key=os.getenv("DEEPSEEK_API_KEY"))

retrieved_content = get_code_snippet()

question = f"""
请基于以下代码片段描述可能的错误处理机制：
{retrieved_content}
注意：请提供多个不同的分析视角，涵盖输入异常、权限控制、调用链等方面。
"""

response = client.chat.completions.create(
    model="deepseek-chat",
    messages=[
        {"role": "system", "content": "你是一个有帮助的代码分析助手"},
        {"role": "user", "content": question}
    ],
    temperature=0.5,
    max_tokens=2048,
    stream=False
)

for i, choice in enumerate(response.choices):
    print(f"候选分析 {i+1}:{choice.message.content.strip()}\n")