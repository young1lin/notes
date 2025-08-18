from unstructured.partition.text import partition_text
text = "90-文档-Data/黑悟空/设定.txt"
elements = partition_text(text)
for element in elements:
    print(element)

# 使用__dict__来查看所有可用的元数据
for i, element in enumerate(elements):
    print(f"\n--- Element {i+1} ---")
    print(f"类型: {type(element)}")
    print(f"元素类: {element.__class__.__name__}")
    print(f"文本内容: {element.text}")
    
    if hasattr(element, 'metadata'):
        print("元数据:")
        metadata_dict = element.metadata.__dict__
        for key, value in metadata_dict.items():
            if not key.startswith('_') and value is not None:  
                print(f"  {key}: {value}")
