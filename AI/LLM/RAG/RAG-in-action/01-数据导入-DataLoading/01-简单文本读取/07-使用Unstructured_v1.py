from unstructured.partition.text import partition_text
text = "90-文档-Data/黑悟空/设定.txt"
elements = partition_text(text)
for element in elements:
    print(element)

# 通过vars函数查看所有可用的元数据
for i, element in enumerate(elements):
    print(f"\n--- Element {i+1} ---")
    print(f"类型: {type(element)}")
    print(f"元素类型: {element.__class__.__name__}")
    print(f"文本内容: {element.text}")
    
    # 元数据展示
    if hasattr(element, 'metadata'):
        print("元数据:")
        metadata = vars(element.metadata)
        valid_metadata = {k: v for k, v in metadata.items() 
                         if not k.startswith('_') and v is not None}
        for key, value in valid_metadata.items():
            print(f"  {key}: {value}")
