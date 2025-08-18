from langchain_community.document_loaders import JSONLoader
print("=== JSONLoader 加载结果 ===")
print("1. 主角信息：")
main_loader = JSONLoader(
    file_path="90-文档-Data/灭神纪/人物角色.json",
    jq_schema='.mainCharacter | "姓名：" + .name + "，背景：" + .backstory',
    text_content=True
)
main_char = main_loader.load()
print(main_char)
print("\n2. 支持角色信息：")
support_loader = JSONLoader(
    file_path="90-文档-Data/灭神纪/人物角色.json",
    jq_schema='.supportCharacters[] | "姓名：" + .name + "，背景：" + .background',
    text_content=True
)
support_chars = support_loader.load()
print(support_chars)

