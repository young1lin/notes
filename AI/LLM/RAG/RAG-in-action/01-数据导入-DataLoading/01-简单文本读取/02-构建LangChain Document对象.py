from langchain_core.documents import Document
documents = [
    Document(
        page_content="悟空是大师兄.",
        metadata={"source": "师徒四人.txt"},
    ),
    Document(
        page_content="八戒是二师兄.",
        metadata={"source": "师徒四人.txt "},
    ),
]
print(documents)

