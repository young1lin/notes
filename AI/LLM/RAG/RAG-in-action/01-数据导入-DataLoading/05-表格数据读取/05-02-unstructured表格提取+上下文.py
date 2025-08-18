from unstructured.partition.pdf import partition_pdf
from llama_index.core import Settings
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding

# 全局设置
Settings.llm = OpenAI(model="gpt-3.5-turbo")
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small")

# 解析 PDF 结构，提取文本和表格
file_path = "90-文档-Data/复杂PDF/billionaires_page-1-5.pdf"  # 修改为你的文件路径

elements = partition_pdf(
    file_path,
    # strategy="hi_res",  # 使用高精度策略
)  # 解析PDF文档

# 创建一个元素ID到元素的映射
element_map = {element.id: element for element in elements if hasattr(element, 'id')}

# 创建一个元素索引到元素的映射
element_index_map = {i: element for i, element in enumerate(elements)}

for i, element in enumerate(elements):
    if element.category == "Table":
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
                print(f"父节点元数据: {vars(parent_element.metadata)}")
        else:
            print(f"未找到父节点 (ID: {parent_id})")
            
        # 打印表格前3个节点的内容
        print("\n表格前3个节点内容:")
        for j in range(max(0, i-3), i):
            prev_element = element_index_map.get(j)
            if prev_element:
                print(f"节点 {j} ({prev_element.category}):")
                print(prev_element.text)
                
        print("-" * 50)

text_elements = [el for el in elements if el.category == "Text"]
table_elements = [el for el in elements if el.category == "Table"]


