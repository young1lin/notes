from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core import VectorStoreIndex
from llama_index.core import Settings
from llama_index.readers.file import PDFReader
from llama_index.core.node_parser import SentenceSplitter

from dotenv import load_dotenv
load_dotenv()   

import os

embed_model = OpenAIEmbedding(model="text-embedding-3-small")
llm = OpenAI(model="gpt-3.5-turbo-0125")

Settings.embed_model = embed_model
Settings.llm = llm
Settings.node_parser = SentenceSplitter(chunk_size=250, chunk_overlap=20) # 50, 100, 250将得到不同的结果，为什么？

# Load PDF using standard PDFReader
loader = PDFReader()
documents = loader.load_data(
    file="90-文档-Data/复杂PDF/uber_10q_march_2022_page26.pdf"
)

# Create index directly from documents
index = VectorStoreIndex.from_documents(documents)

# Create query engine
query_engine = index.as_query_engine(
    similarity_top_k=3,
    verbose=True
)

query = "how much is the Loss from operations for 2022?"

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