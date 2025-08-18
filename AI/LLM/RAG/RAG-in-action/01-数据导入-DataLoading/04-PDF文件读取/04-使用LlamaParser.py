# 需要LLAMA_CLOUD_API_KEY
from dotenv import load_dotenv
load_dotenv()   

# LlamaParse PDF reader for PDF Parsing
from llama_parse import LlamaParse
documents = LlamaParse(result_type="markdown").load_data(
    "90-文档-Data/黑悟空/黑神话悟空.pdf"
)
print(documents)

from llama_index.core.node_parser import MarkdownElementNodeParser
node_parser = MarkdownElementNodeParser()
nodes = node_parser.get_nodes_from_documents(documents)

print(nodes)

