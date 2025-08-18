file_path = '90-文档-Data/山西文旅/云冈石窟-en.pdf'
from langchain_unstructured import UnstructuredLoader
loader = UnstructuredLoader(
    file_path=file_path,
    strategy="hi_res",
    # partition_via_api=True, # 通过API调用Unstructured
    # coordinates=True, # 返回元素位置坐标
)
docs = []
for doc in loader.lazy_load():
    docs.append(doc)


# 仅筛选第一页的 Doc
page_number = 1
page_docs = [doc for doc in docs if doc.metadata.get("page_number") == page_number]

# 遍历并打印每个 Doc 的详细信息
for i, doc in enumerate(page_docs, 1):
    print(f"Doc {i}:")
    print(f"  内容: {doc.page_content}")
    print(f"  分类: {doc.metadata.get('category')}")
    print(f"  ID: {doc.metadata.get('element_id')}")
    print(f"  Parent ID: {doc.metadata.get('parent_id')}")
    # print(f"  位置: {doc.metadata.get('position')}")
    # print(f"  坐标: {doc.metadata.get('coordinates')}")
    print("="*50)

# 仅筛选第一页的 Title
page_number = 1
title_dict = {}

# 收集 Title，并建立 parent_id -> Title 的映射
for doc in docs:
    if doc.metadata.get("category") == "Title" and doc.metadata.get("page_number") == page_number:
        title_id = doc.metadata.get("element_id")  # Title 的唯一 ID
        title_text = doc.page_content.strip()  # 添加 strip() 去除空白字符
        # 避免重复添加相同的标题
        if title_text not in [data["title"] for data in title_dict.values()]:
            title_dict[title_id] = {"title": title_text, "content": []}

# 关联 Title 和其对应的 Text
for doc in docs:
    if doc.metadata.get("category") in ["NarrativeText", "Text"] and doc.metadata.get("page_number") == page_number:  # 修改这里以包含 NarrativeText
        parent_id = doc.metadata.get("parent_id")
        if parent_id in title_dict:
            content = doc.page_content.strip()  # 添加 strip() 去除空白字符
            if content:  # 只添加非空内容
                title_dict[parent_id]["content"].append(content)

# 命令行输出
for title_data in title_dict.values():
    if title_data["content"]:  # 只输出有内容的标题
        print("\n=== " + title_data["title"] + " ===")
        for content in title_data["content"]:
            print(content)
        print()