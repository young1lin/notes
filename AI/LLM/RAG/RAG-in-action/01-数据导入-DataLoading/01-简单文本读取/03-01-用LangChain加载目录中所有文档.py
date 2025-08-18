# 使用 LangChain 加载目录中所有文档

"""
# 需要安装 Tesseract OCR 
### Ubuntu 执行如下命令：
sudo apt update
sudo apt install tesseract-ocr -y

### 说明

DirectoryLoader 在加载目录时，会尝试为遇到的不同文件类型找到合适的加载器。
对于 .pptx (PowerPoint)、.pdf 和 .jpg 等复杂格式，DirectoryLoader 通常会依赖 unstructured 库来处理。
unstructured 库在处理某些文件类型（特别是需要进行文本提取和处理的）时，会依赖 nltk (Natural Language Toolkit) 库。
nltk 库需要下载一些额外的数据包（如分词器、词性标注器等）才能正常工作。

如果遇到错误 zipfile.BadZipFile: File is not a zip file 
这是发生发生在 nltk.data.py 内部，这强烈表明 nltk 在尝试加载其数据包时遇到了问题。
它尝试打开一个文件作为 zip 压缩包来查找数据，但该文件不是一个有效的 zip 文件。


您好！

根据您提供的错误信息和文件列表，问题出在 langchain_community.document_loaders.DirectoryLoader 尝试加载目录中的 .pptx 文件时。

根本原因分析：

DirectoryLoader 在加载目录时，会尝试为遇到的不同文件类型找到合适的加载器。
对于 .pptx (PowerPoint)、.pdf 和 .jpg 等复杂格式，DirectoryLoader 通常会依赖 unstructured 库来处理。
unstructured 库在处理某些文件类型（特别是需要进行文本提取和处理的）时，会依赖 nltk (Natural Language Toolkit) 库。
nltk 库需要下载一些额外的数据包（如分词器、词性标注器等）才能正常工作。
您的错误 zipfile.BadZipFile: File is not a zip file 发生在 nltk.data.py 内部，这强烈表明 nltk 在尝试加载其数据包时遇到了问题。它尝试打开一个文件作为 zip 压缩包来查找数据，但该文件不是一个有效的 zip 文件。
结论： 错误不是因为您的 .pptx 文件本身损坏，而是因为 unstructured 调用 nltk 时，nltk 找不到或无法正确加载其所需的数据包，导致了 BadZipFile 错误。当只有 .csv 文件时，DirectoryLoader 可能使用了一个不依赖 unstructured 或 nltk 的简单加载器，所以没有报错。

#解决方案：
# 最直接的解决方案是 
# 下载 NLTK 所需的数据包。需要在你的 Python 环境中运行以下代码一次
import nltk
nltk.download('averaged_perceptron_tagger') 
nltk.download('punkt') 
"""
import os
from langchain_community.document_loaders import DirectoryLoader

# 获取当前脚本文件所在的目录
script_dir = os.path.dirname(__file__)
print(f"获取当前脚本文件所在的目录：{script_dir}") 
# 结合相对路径构建完整路径
data_dir = os.path.join(script_dir, '../../90-文档-Data/黑悟空')

loader = DirectoryLoader(data_dir)
docs = loader.load()
print(f"文档数：{len(docs)}")  # 输出文档总数
print(docs[0])  # 输出第一个文档