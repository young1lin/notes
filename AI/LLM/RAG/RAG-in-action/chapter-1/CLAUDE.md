# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Architecture

This is a RAG (Retrieval Augmented Generation) demonstration project with two main implementations:

### Directory Structure
- `llamaindex-in-action/` - LlamaIndex-based RAG implementation
- `langchain-in-action/` - LangChain-based RAG implementation  
- `90-文档-Data/` - Document corpus containing Chinese travel and gaming content
  - `山西文旅/` - Shanxi tourism documents (PDFs, TXT files)
  - `黑悟空/` - Black Myth: Wukong game data and settings

### Key Components

**LlamaIndex Implementation** (`llamaindex-in-action/`):
- `deepseek_rag.py` - Main RAG pipeline using DeepSeek LLM + BGE embeddings
- `config.py` - File path configurations pointing to data sources
- Uses HuggingFace BGE Chinese embedding model (`BAAI/bge-small-zh`)
- Integrates with DeepSeek API for LLM inference

**LangChain Implementation** (`langchain-in-action/`):
- `langchain_deepseek_rag.py` - RAG pipeline using LangChain + DeepSeek
- Web-based document loading from Wikipedia
- Uses BGE embeddings with InMemoryVectorStore
- Text chunking with RecursiveCharacterTextSplitter

## Environment Setup

Both implementations require:
1. `.env` file with `DEEPSEEK_API_KEY` for API access
2. Python dependencies from respective `requirments.txt` files

## Running the Applications

**LlamaIndex version:**
```bash
cd llamaindex-in-action
python deepseek_rag.py
```

**LangChain version:**  
```bash
cd langchain-in-action
python langchain_deepseek_rag.py
```

## Key Dependencies

- **DeepSeek API**: Both implementations use DeepSeek's chat model for text generation
- **BGE Embeddings**: Chinese text embeddings via HuggingFace models
- **Document Processing**: PDF/TXT file parsing and web scraping capabilities
- **Vector Storage**: In-memory vector stores for similarity search

## Data Sources

The project focuses on Chinese language content:
- Tourism information about Shanxi Province attractions
- Black Myth: Wukong game content and settings
- Documents are processed to answer domain-specific questions in Chinese

## Model Configuration

Both implementations use:
- **LLM**: `deepseek-chat` model via API
- **Embeddings**: `BAAI/bge-small-zh` or `BAAI/bge-small-zh-v1.5` 
- **Device**: CUDA support configured for embeddings when available