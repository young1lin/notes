from langchain_unstructured import UnstructuredLoader
from typing import List
from langchain_core.documents import Document
page_url = "https://zh.wikipedia.org/wiki/黑神话：悟空"
def _get_setup_docs_from_url(url: str) -> List[Document]:
    loader = UnstructuredLoader(web_url=url)
    setup_docs = []
    # parent_id = None  # 初始化 parent_id
    # current_parent = None  # 用于存储当前父元素
    for doc in loader.load():
        # 检查是否是 Title 或 Table
        if doc.metadata["category"] == "Title" or doc.metadata["category"] == "Table":
            parent_id = doc.metadata["element_id"]
            current_parent = doc  # 更新当前父元素
            setup_docs.append(doc)
        elif doc.metadata.get("parent_id") == parent_id:
            setup_docs.append((current_parent, doc))  # 将父元素和子元素一起存储
    return setup_docs       
docs = _get_setup_docs_from_url(page_url)
for item in docs:
    if isinstance(item, tuple):
        parent, child = item
        print(f'父元素 - {parent.metadata["category"]}: {parent.page_content}')
        print(f'子元素 - {child.metadata["category"]}: {child.page_content}')
    else:
        print(f'{item.metadata["category"]}: {item.page_content}')
    print("-" * 80)

