from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core import VectorStoreIndex
from llama_index.core import Settings

from dotenv import load_dotenv
load_dotenv()   

embed_model = OpenAIEmbedding(model="text-embedding-3-small")
llm = OpenAI(model="gpt-3.5-turbo-0125")

Settings.llm = llm
Settings.embed_model = embed_model

# LlamaParse PDF reader for PDF Parsing
from llama_parse import LlamaParse
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

from llama_index.postprocessor.flag_embedding_reranker import (
    FlagEmbeddingReranker,
)

reranker = FlagEmbeddingReranker(
    top_n=5,
    model="BAAI/bge-reranker-large",
)

recursive_query_engine = recursive_index.as_query_engine(
    similarity_top_k=15, node_postprocessors=[reranker], verbose=True
)

raw_query_engine = raw_index.as_query_engine(
    similarity_top_k=3, node_postprocessors=[reranker]
)


query = "What is the change of free cash flow and what is the rate from the financial and operational highlights?"
query = "how many COVID-19 response initiatives in year 2021?"
query = "After the year of COVID-19, how much EBITDA profit improved?"

response_1 = raw_query_engine.query(query)
print("\n************New LlamaParse+ Basic Query Engine************")
print(response_1)

# Display retrieved chunks
print("\n************Retrieved Text Chunks************")
for i, source_node in enumerate(response_1.source_nodes):
    print(f"\nChunk {i+1}:")
    print("Text content:")
    print(source_node.text)
    print("-" * 50)

response_2 = recursive_query_engine.query(query)
print(
    "\n************New LlamaParse+ Recursive Retriever Query Engine************"
)
print(response_2)

# Display retrieved chunks
print("\n************Retrieved Text Chunks************")
for i, source_node in enumerate(response_2.source_nodes):
    print(f"\nChunk {i+1}:")
    print("Text content:")
    print(source_node.text)
    print("-" * 50)