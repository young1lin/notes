# 连接到SQLite数据库
import sqlite3
conn = sqlite3.connect('90-文档-Data/tourism.db')
cursor = conn.cursor()
# 创建景区信息表
cursor.execute('''
CREATE TABLE IF NOT EXISTS scenic_spots (
    scenic_id INTEGER PRIMARY KEY,
    scenic_name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    level VARCHAR(20),
    monthly_visitors INTEGER
)''')
# 创建城市信息表
cursor.execute('''
CREATE TABLE IF NOT EXISTS city_info (
    city_id INTEGER PRIMARY KEY,
    city_name VARCHAR(50) NOT NULL,
    annual_tourism_income INTEGER,
    famous_dish VARCHAR(100)
)''')
# 插入示例数据到景区信息表
sample_scenic_spots = [
    (1, '晋祠', '太原市', 'AAAAA', 50000),
    (2, '五台山', '忻州市', 'AAAAA', 80000),
    (3, '云冈石窟', '大同市', 'AAAAA', 70000),
    (4, '平遥古城', '晋中市', 'AAAAA', 90000),
    (5, '乔家大院', '晋中市', 'AAAA', 45000)
]
cursor.executemany('INSERT OR REPLACE INTO scenic_spots VALUES (?, ?, ?, ?, ?)', sample_scenic_spots)
# 插入示例数据到城市信息表
sample_city_info = [
    (1, '太原市', 200000000, '刀削面'),
    (2, '大同市', 180000000, '大同醋'),
    (3, '晋中市', 150000000, '臊子面'),
    (4, '忻州市', 120000000, '莜面栲栳栳'),
    (5, '运城市', 130000000, '运城煮饼')
]
cursor.executemany('INSERT OR REPLACE INTO city_info VALUES (?, ?, ?, ?)', sample_city_info)
# 提交更改并关闭连接
conn.commit()
conn.close()
print("数据库表创建完成，并已插入示例数据。")
