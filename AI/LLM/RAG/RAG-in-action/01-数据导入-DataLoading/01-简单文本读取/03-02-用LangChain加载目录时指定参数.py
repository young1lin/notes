from langchain_community.document_loaders import DirectoryLoader

import os
# 获取当前脚本文件所在的目录
script_dir = os.path.dirname(__file__)
print(f"获取当前脚本文件所在的目录：{script_dir}") 
# 结合相对路径构建完整路径
data_dir = os.path.join(script_dir, '../../90-文档-Data/黑悟空')

loader = DirectoryLoader(data_dir, 
                         glob="**/*.md", 
                         use_multithreading=True,
                         show_progress=True,
                         )
docs = loader.load()
print(f"文档数：{len(docs)}")  # 输出文档总数
print(docs[0])  # 输出第一个文档