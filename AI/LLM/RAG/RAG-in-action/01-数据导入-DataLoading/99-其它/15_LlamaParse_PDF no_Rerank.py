from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core import VectorStoreIndex
from llama_index.core import Settings

from dotenv import load_dotenv
load_dotenv()   

import os

# API access to llama-cloud
os.environ["LLAMA_CLOUD_API_KEY"] = "llx-9LWXKaFjF7DgO3clPLL70nxb3sfngHXHbf5bZpnZSFvGNkcp"

embed_model = OpenAIEmbedding(model="text-embedding-3-small")
llm = OpenAI(model="gpt-3.5-turbo-0125")

Settings.llm = llm
Settings.embed_model = embed_model

# LlamaParse PDF reader for PDF Parsing
from llama_parse import LlamaParse

parser = LlamaParse(api_key=os.getenv("LLAMA_CLOUD_API_KEY"), result_type='markdown')

documents = LlamaParse(result_type="markdown").load_data(
    "data/PDF/uber_10q_march_2022.pdf"
)
# Started parsing the file under job_id b76a572b-d2bb-42ae-bad9-b9810049f1af


from llama_index.core.node_parser import MarkdownElementNodeParser

node_parser = MarkdownElementNodeParser(
    llm=OpenAI(model="gpt-3.5-turbo-0125"), num_workers=8
)

nodes = node_parser.get_nodes_from_documents(documents)


text_nodes, index_nodes = node_parser.get_nodes_and_objects(nodes)
text_nodes[0]
index_nodes[0]


recursive_index = VectorStoreIndex(nodes=text_nodes + index_nodes)
raw_index = VectorStoreIndex.from_documents(documents)


recursive_query_engine = recursive_index.as_query_engine(
    similarity_top_k=15,  verbose=True
)

raw_query_engine = raw_index.as_query_engine(
    similarity_top_k=15,
)


query = "What is the change of free cash flow and what is the rate from the financial and operational highlights?"
query = "how many COVID-19 response initiatives in year 2021?"

response_1 = raw_query_engine.query(query)
print("\n************New LlamaParse+ Basic Query Engine************")
print(response_1)

response_2 = recursive_query_engine.query(query)
print(
    "\n************New LlamaParse+ Recursive Retriever Query Engine************"
)
print(response_2)