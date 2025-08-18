import textract
text = textract.process("data/黑悟空/黑神话悟空.pdf")
print(text)

# Textract 与 PyMuPDF、Unstructured 对比:

# 优势:
# 1. 支持多种文档格式(不仅PDF,还包括doc、odt等)
# 2. 使用简单,API统一
# 3. 自动检测文档编码
# 4. 不需要额外的依赖库
# 5. 适合快速提取文本内容

# 劣势:
# 1. 功能相对简单,只能提取文本
# 2. 无法获取文档结构信息
# 3. 不支持图片提取
# 4. 对复杂布局的处理能力较弱
# 5. 无法获取元数据
# 6. 处理大文件时性能较差
