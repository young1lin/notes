from llama_index.readers.database import DatabaseReader

# 数据库创建和表结构说明：
# 1. 创建数据库: CREATE DATABASE example_db;
# 2. 使用数据库: USE example_db;
# 3. 创建黑神话悟空游戏场景表:
#    CREATE TABLE game_scenes (
#      id INT AUTO_INCREMENT PRIMARY KEY,
#      scene_name VARCHAR(100) NOT NULL,
#      description TEXT,
#      difficulty_level INT,
#      boss_name VARCHAR(100),
#      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
#    );
# 4. 插入黑神话悟空游戏场景数据:
#    INSERT INTO game_scenes (scene_name, description, difficulty_level, boss_name)
#    VALUES 
#      ('花果山', '悟空的出生地，山清水秀，仙气缭绕', 2, '六耳猕猴'),
#      ('水帘洞', '花果山中的洞穴，悟空的老家', 1, NULL),
#      ('火焰山', '炙热难耐的火山地带，充满岩浆与烈焰', 4, '牛魔王'),
#      ('龙宫', '东海龙王的宫殿，水下奇景', 3, '敖广'),
#      ('灵山', '如来佛祖居住的圣地，佛光普照', 5, '如来佛祖');

reader = DatabaseReader(
    scheme="mysql",
    host="localhost",
    port=3306,
    user="newuser",
    password="password",
    dbname="example_db"
)

query = "SELECT * FROM game_scenes" # 选择所有游戏场景 -> Text-to-SQL
documents = reader.load_data(query=query)

print(f"从数据库加载的文档数量: {len(documents)}")
print(documents)

# 参考文献
# https://docs.llamaindex.ai/en/stable/examples/index_structs/struct_indices/SQLIndexDemo/