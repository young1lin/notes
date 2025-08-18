import pymupdf
# 打开PDF文件
doc = pymupdf.open("90-文档-Data/黑悟空/黑神话悟空.pdf")
text = [page.get_text() for page in doc]
print(text)

# 示例: 使用PyMuPDF的基础功能
print("=== PyMuPDF 基本信息提取 ===")
print(f"文档页数: {len(doc)}")
print(f"文档标题: {doc.metadata['title']}")
print(f"文档作者: {doc.metadata['author']}")
print(f"文档元数据: {doc.metadata}")  # 比Unstructured提供更多元数据

# 遍历每一页
for page_num, page in enumerate(doc):
    # 提取文本
    text = page.get_text()
    print(f"\n--- 第{page_num + 1}页 ---")
    print("文本内容:", text[:200])  # 显示前200个字符
    
    # 提取图片
    images = page.get_images()
    print(f"图片数量: {len(images)}")
    
    # 获取页面链接
    links = page.get_links()
    print(f"链接数量: {len(links)}")
    
    # 获取页面大小
    width, height = page.rect.width, page.rect.height
    print(f"页面尺寸: {width} x {height}")

doc.close()

# PyMuPDF (fitz) 与 Unstructured 对比:
# 优势:
# 1. 更快的处理速度
# 2. 更细粒度的PDF控制能力
# 3. 可以获取更多元数据和文档结构信息
# 4. 内存占用更少
# 5. 不依赖外部工具

# 劣势:
# 1. 文本提取的智能化程度较低
# 2. 没有自动的文档结构理解
# 3. 需要手动处理布局分析