# 导入unstructured的partition函数用于PDF解析
from unstructured.partition.auto import partition

# 设置PDF文件路径
filename = "90-文档-Data/黑悟空/黑神话悟空.pdf"

# 使用partition函数解析PDF文件
# content_type指定文件类型为PDF
elements = partition(filename=filename, 
                   content_type="application/pdf"
                  )

# 展示解析出的elements的类型和内容
print("PDF解析后的Elements类型:")
for i, element in enumerate(elements[:5]):
    print(f"\nElement {i+1}:")
    print(f"类型: {type(element).__name__}")
    print(f"内容: {str(element)}")
    print("-" * 50)

# 统计不同类型elements的数量
element_types = {}
for element in elements:
    element_type = type(element).__name__
    element_types[element_type] = element_types.get(element_type, 0) + 1

print("\nElements类型统计:")
for element_type, count in element_types.items():
    print(f"{element_type}: {count}个")

