# 把PDF的某几页提取出来，保存为同一目录新的PDF后缀页面
from pathlib import Path
from pypdf import PdfReader, PdfWriter

def extract_pages(pdf_path, output_path, page_numbers):
    """
    提取指定页码的PDF页面并保存为新的PDF文件            
    """
    try:
        # 确保输出目录存在
        output_dir = Path(output_path).parent
        output_dir.mkdir(parents=True, exist_ok=True)
        
        # 打开原始PDF文件
        reader = PdfReader(pdf_path)
        writer = PdfWriter()
        
        # 提取指定页码
        for page_number in page_numbers:
            if 1 <= page_number <= len(reader.pages):
                writer.add_page(reader.pages[page_number - 1])
            else:
                print(f"警告：页码 {page_number} 超出范围，PDF共有 {len(reader.pages)} 页")
                
        # 保存新的PDF文件
        with open(output_path, 'wb') as output_file:
            writer.write(output_file)
            
        print(f"成功提取页面 {page_numbers} 到 {output_path}")
            
    except Exception as e:
        print(f"处理PDF时发生错误: {str(e)}")
        raise
            
if __name__ == "__main__":
    pdf_path = "90-文档-Data/复杂PDF/uber_10q_march_2022.pdf"
    output_path = "90-文档-Data/复杂PDF/uber_10q_march_2022_page1-3.pdf"
    page_numbers = [26, 27, 28]  # 指定要提取的页码
    extract_pages(pdf_path, output_path, page_numbers)
        