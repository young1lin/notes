from unstructured.documents.elements import Title, NarrativeText, Text
from unstructured.partition.pdf import partition_pdf

file_path = '90-文档-Data/山西文旅/云冈石窟-en.pdf'

# 使用 unstructured 直接读取 PDF
elements = partition_pdf(
    filename=file_path,
    strategy="hi_res",
    # include_metadata=True,  # 如果需要位置信息
)

print(elements[0].to_dict())

# 添加调试信息，查看第一个元素的完整信息
if elements:
    first_elem = elements[0]
    print("=== 第一个元素的详细信息 ===")
    print(f"类型: {type(first_elem)}")
    print(f"文本: {first_elem.text}")
    print("Metadata 属性:")
    print(vars(first_elem.metadata))  # 打印所有 metadata 属性
    print("元素的所有属性:")
    print(vars(first_elem))  # 打印元素的所有属性
    print("="*50)

# 仅筛选第一页的元素
page_number = 1
page_elements = [elem for elem in elements if getattr(elem.metadata, "page_number", None) == page_number]

# 遍历并打印每个元素的详细信息
for i, elem in enumerate(page_elements, 1):
    print(f"\nElement {i}:")
    print(f"  内容: {elem.text}")
    print(f"  分类: {type(elem).__name__}")
    print(f"  ID: {getattr(elem, '_element_id', None)}")
    print("="*50)

# 仅筛选第一页的 Title
title_dict = {}

# 收集 Title，并建立 parent_id -> Title 的映射
for elem in elements:
    if (isinstance(elem, Title) and 
        getattr(elem.metadata, "page_number", None) == page_number):
        title_id = getattr(elem, '_element_id', None) # 获取元素的 ID 单独id亦可
        title_text = elem.text.strip()
        if title_text not in [data["title"] for data in title_dict.values()]:
            title_dict[title_id] = {"title": title_text, "content": []}

# 关联 Title 和其对应的 Text
for elem in elements:
    if (isinstance(elem, (NarrativeText, Text)) and 
        getattr(elem.metadata, "page_number", None) == page_number):
        parent_id = getattr(elem.metadata, "parent_id", None)
        if parent_id in title_dict:
            content = elem.text.strip()
            if content:  # 只添加非空内容
                title_dict[parent_id]["content"].append(content)

# 命令行输出
for title_data in title_dict.values():
    if title_data["content"]:  # 只输出有内容的标题
        print("\n=== " + title_data["title"] + " ===")
        for content in title_data["content"]:
            print(content)
        print() 