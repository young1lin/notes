"""
使用 unstructured 库进行 PDF 表格提取

【系统依赖安装】
运行此脚本前，需要安装以下系统依赖：

1. 安装 poppler-utils (用于PDF处理):
   sudo apt update
   sudo apt install -y poppler-utils

2. 安装 Tesseract OCR (用于文字识别):
   sudo apt install -y tesseract-ocr tesseract-ocr-chi-sim tesseract-ocr-eng

【常见错误解决方案】
- 错误: "PDFInfoNotInstalledError: Unable to get page count. Is poppler installed and in PATH?"
  解决: 安装 poppler-utils

- 错误: "TesseractNotFoundError: tesseract is not installed or it's not in your PATH"
  解决: 安装 tesseract-ocr

- 错误: "No such file or directory" 或 中文路径显示为编码
  解决: 确保在项目根目录运行脚本，或使用绝对路径

【验证安装】
可以使用以下命令验证安装是否成功：
- pdfinfo -v
- tesseract --version

【VSCode运行注意事项】
- 确保VSCode终端工作目录在项目根目录
- 如果遇到中文路径编码问题，建议在VSCode终端中运行脚本
- 推荐在VSCode中设置Python解释器为正确的虚拟环境

【Python依赖】
91-环境-Environment/requirements_llamaindex_Ubuntu-with-CPU.txt

"""

import os
import sys
from pathlib import Path
from unstructured.partition.pdf import partition_pdf

# 确保工作目录正确
# 获取脚本所在目录的父目录（项目根目录）
script_dir = Path(__file__).parent.parent.parent
if script_dir.exists():
    os.chdir(script_dir)
    print(f"工作目录设置为: {os.getcwd()}")

# 导入 LlamaIndex 相关模块
from llama_index.core import Settings
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding

# 全局设置
Settings.llm = OpenAI(model="gpt-3.5-turbo")
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# 解析 PDF 结构，提取文本和表格
# 使用相对路径，确保从项目根目录开始
file_path = "90-文档-Data/复杂PDF/billionaires_page-1-5.pdf"

# 检查文件是否存在
if not os.path.exists(file_path):
    print(f"错误: 文件不存在 - {file_path}")
    print(f"当前工作目录: {os.getcwd()}")
    print("请确保:")
    print("1. 在项目根目录运行脚本")
    print("2. PDF文件路径正确")
    sys.exit(1)

print(f"正在处理文件: {file_path}")

elements = partition_pdf(
    file_path,
    strategy="hi_res",  # 使用高精度策略
)  # 解析PDF文档

# 创建一个元素ID到元素的映射
element_map = {element.id: element for element in elements if hasattr(element, 'id')}

for element in elements:
    if element.category == "Table": # 只打印表格数据    
        print("\n表格数据:")
        print("表格元数据:", vars(element.metadata))  # 使用vars()显示所有元数据属性
        print("表格内容:")
        print(element.text)  # 打印表格文本内容
        
        # 获取并打印父节点信息
        parent_id = getattr(element.metadata, 'parent_id', None)
        if parent_id and parent_id in element_map:
            parent_element = element_map[parent_id]
            print("\n父节点信息:")
            print(f"类型: {parent_element.category}")
            print(f"内容: {parent_element.text}")
            if hasattr(parent_element, 'metadata'):
                print(f"父节点元数据: {vars(parent_element.metadata)}")  # 同样使用vars()显示所有元数据
        else:
            print(f"未找到父节点 (ID: {parent_id})")
        print("-" * 50)

text_elements = [el for el in elements if el.category == "Text"]
table_elements = [el for el in elements if el.category == "Table"]


