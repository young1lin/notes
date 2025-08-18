from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core import VectorStoreIndex
from llama_index.core import Settings
from llama_index.readers.file import PDFReader

from dotenv import load_dotenv
load_dotenv()   

import os

embed_model = OpenAIEmbedding(model="text-embedding-3-small")
llm = OpenAI(model="gpt-3.5-turbo-0125")

Settings.llm = llm
Settings.embed_model = embed_model

# Load PDF using standard PDFReader
loader = PDFReader()
documents = loader.load_data(
    file="data/PDF/uber_10q_march_2022.pdf"
)

# Create index directly from documents
index = VectorStoreIndex.from_documents(documents)

# Create query engine
query_engine = index.as_query_engine(
    similarity_top_k=3,
    verbose=True
)

query = "What is the change of free cash flow and what is the rate from the financial and operational highlights?"
query = "how many COVID-19 response initiatives in year 2021?"
query = "how much the COVID-19 response initiatives inpact the EBITDA?" # 重塑问题的重要性
# query = "After the year of COVID-19, how much EBITDA profit improved?"
# query = "What is the Adjusted EBITDA loss in year COVID-19?"
# query = "how much is the Loss from operations?"
# query = "how much is the Loss from operations for 2021?"


response = query_engine.query(query)
print("\n************LlamaIndex Query Response************")
print(response)

# Display retrieved chunks
print("\n************Retrieved Text Chunks************")
for i, source_node in enumerate(response.source_nodes):
    print(f"\nChunk {i+1}:")
    print("Text content:")
    print(source_node.text)
    print("-" * 50)