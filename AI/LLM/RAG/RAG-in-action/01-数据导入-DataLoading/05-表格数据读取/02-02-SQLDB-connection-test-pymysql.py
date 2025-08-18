import pymysql

try:
    connection = pymysql.connect(
        host="localhost",
        user="newuser",
        password="password",
        database="example_db",
        port=3306
    )

    with connection.cursor() as cursor:
        cursor.execute("SELECT * FROM game_scenes")
        for row in cursor.fetchall():
            print(row)

    connection.close()

except Exception as e:
    print("数据库连接失败:", e)


