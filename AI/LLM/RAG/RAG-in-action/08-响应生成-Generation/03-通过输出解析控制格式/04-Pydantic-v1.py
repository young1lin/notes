from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import datetime

# 定义一个用户模型
class User(BaseModel):
    id: int
    username: str = Field(min_length=3, max_length=20)
    email: str = Field(pattern=r"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$")
    age: Optional[int] = Field(gt=0, lt=120)
    created_at: datetime = Field(default_factory=datetime.now)
    tags: List[str] = Field(default_factory=list)

# 创建一个用户实例
user_data = {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "age": 25,
    "tags": ["python", "developer"]
}

# 验证数据
try:
    user = User(**user_data)
    print("用户数据验证成功！")
    print(f"用户名: {user.username}")
    print(f"邮箱: {user.email}")
    print(f"年龄: {user.age}")
    print(f"创建时间: {user.created_at}")
    print(f"标签: {user.tags}")
    
    # 转换为字典
    print("\n转换为字典:")
    print(user.model_dump())
    
    # 转换为JSON
    print("\n转换为JSON:")
    print(user.model_dump_json())
    
except Exception as e:
    print(f"数据验证失败: {e}") 