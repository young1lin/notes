import pandas as pd
import os

# 定义要处理的CSV文件列表
csv_files = [
    'billionaires_table_2.csv',
    'billionaires_table_3.csv',
    'billionaires_table_4.csv',
    'billionaires_table_5.csv',
    'billionaires_table_6.csv'
]

# 创建一个ExcelWriter对象
with pd.ExcelWriter('billionaires_merged.xlsx', engine='openpyxl') as writer:
    # 遍历每个CSV文件
    for csv_file in csv_files:
        # 读取CSV文件
        df = pd.read_csv(csv_file)
        
        # 获取sheet名称（去掉.csv后缀）
        sheet_name = os.path.splitext(csv_file)[0]
        
        # 将数据写入Excel的对应sheet
        df.to_excel(writer, sheet_name=sheet_name, index=False)

print("CSV文件已成功合并到 billionaires_merged.xlsx") 