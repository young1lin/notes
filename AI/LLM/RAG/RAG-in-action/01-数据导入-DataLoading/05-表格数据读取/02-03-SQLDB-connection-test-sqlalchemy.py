from sqlalchemy import create_engine, text
import pandas as pd

# 确保使用 pymysql 作为驱动
engine = create_engine("mysql+pymysql://newuser:password@localhost:3306/example_db")

# 测试连接
try:
    with engine.connect() as connection:
        # 使用 text() 函数包装 SQL 语句
        result = connection.execute(text("SELECT * FROM game_scenes"))
        df = pd.DataFrame(result.fetchall(), columns=result.keys())
        print("查询结果：")
        print(df)
except Exception as e:
    print("数据库连接失败:", e)



