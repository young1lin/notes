"""
Q: 在使用 unstructured 解析 PPT 等 Office 文件时，如果出现 FileNotFoundError: soffice command was not found 错误怎么办？
A: 这是因为系统中缺少 libreoffice。unstructured 需要调用 libreoffice 的 soffice 命令来处理 Office 文档。
解决方案是在系统中安装 libreoffice。
在 Debian/Ubuntu 系统中，可以使用以下命令安装：
sudo apt-get update && sudo apt-get install -y libreoffice

- Install instructions: https://www.libreoffice.org/get-help/install-howto/
- Mac: https://formulae.brew.sh/cask/libreoffice
- Debian: https://wiki.debian.org/LibreOffice
"""
from unstructured.partition.ppt import partition_ppt
# 解析 PPT 文件
ppt_elements = partition_ppt(filename="90-文档-Data/黑悟空/黑神话悟空.pptx")
print("PPT 内容：")
# for element in ppt_elements:
#     print(element.text)
    
from langchain_core.documents import Document
# 转换为 Documents 数据结构
documents = [
Document(page_content=element.text, 
  	     metadata={"source": "data/黑神话悟空PPT.pptx"})
    for element in ppt_elements
]

# 输出转换后的 Documents
print(documents[0:3])


