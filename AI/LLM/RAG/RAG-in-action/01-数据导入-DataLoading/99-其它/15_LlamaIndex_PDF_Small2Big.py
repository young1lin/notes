from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core import VectorStoreIndex, Settings
from llama_index.readers.file import PDFReader
from llama_index.core.node_parser import SentenceWindowNodeParser
from llama_index.core.postprocessor import MetadataReplacementPostProcessor
from llama_index.core.node_parser import SentenceSplitter

from dotenv import load_dotenv
load_dotenv()   

import os

# Setup LLM and embedding models
embed_model = OpenAIEmbedding(model="text-embedding-3-small")
llm = OpenAI(model="gpt-3.5-turbo-0125")

Settings.llm = llm
Settings.embed_model = embed_model
Settings.node_parser = SentenceSplitter(chunk_size=72, chunk_overlap=20)

# Create sentence window parser
node_parser = SentenceWindowNodeParser.from_defaults(
    window_size=3,
    window_metadata_key="window",
    original_text_metadata_key="original_text",
)

# Load PDF and parse into nodes with sentence windows
loader = PDFReader()
documents = loader.load_data(file="data/PDF/uber_10q_march_2022.pdf")
nodes = node_parser.get_nodes_from_documents(documents)

# Create index from nodes
index = VectorStoreIndex(nodes)

# Create query engine with metadata replacement
query_engine = index.as_query_engine(
    similarity_top_k=3,
    node_postprocessors=[
        MetadataReplacementPostProcessor(target_metadata_key="window")
    ],
    verbose=True
)

# Example queries
query = "What is the change of free cash flow and what is the rate from the financial and operational highlights?"
query = "how much COVID-19 response initiatives in millions in year 2021?" # 这个问题LC会LI不会
query = "What is the Adjusted EBITDA loss in year COVID-19?"
query = "how much is the Loss from operations for the period ended March 31, 2021?"
query = "how much is the Loss from operations for 2022?"

response = query_engine.query(query)
print("\n************LlamaIndex Query Response************")
print(response)

# Display retrieved chunks
print("\n************Retrieved Text Chunks************")
for i, source_node in enumerate(response.source_nodes):
    print(f"\nChunk {i+1}:")
    print("Original sentence:")
    print(source_node.node.metadata["original_text"])
    print("\nContext window:")
    print(source_node.node.metadata["window"])
    print("-" * 50)