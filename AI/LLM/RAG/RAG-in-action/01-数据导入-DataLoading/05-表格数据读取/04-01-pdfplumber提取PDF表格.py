import pdfplumber
import pandas as pd
import time

# 记录开始时间
start_time = time.time()

# 打开PDF文件
pdf = pdfplumber.open("90-文档-Data/复杂PDF/billionaires_page-1-5.pdf")

# 遍历每一页
for page in pdf.pages:
    # 提取表格
    tables = page.extract_tables()
    
    # 检查是否找到表格
    if tables:
        print(f"在第 {page.page_number} 页找到 {len(tables)} 个表格")
        
        # 遍历该页的所有表格
        for i, table in enumerate(tables):
            print(f"\n处理第 {i+1} 个表格:")
            
            # 将表格转换为DataFrame
            df = pd.DataFrame(table)
            
            # 如果第一行包含列名，可以设置为列名
            if len(df) > 0:
                df.columns = df.iloc[0]
                df = df.iloc[1:]  # 删除重复的列名行
                
            print(df)
            print("-" * 50)

# 关闭PDF
pdf.close()

# 记录结束时间并计算总耗时
end_time = time.time()
print(f"\nPDF表格提取总耗时: {end_time - start_time:.2f}秒")