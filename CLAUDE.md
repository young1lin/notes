# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

`CLAUDE.md` is the primary rule file for this repository.

`AGENTS.md` is a consolidated companion document that combines the guidance here with imported writing and evidence rules for broader agent compatibility.

## Repository Overview

This is a personal knowledge base managed with Obsidian, containing technical notes, learning materials, and code examples across various software engineering domains. The repository is bilingual (Chinese/English) with content organized by topic area.

## Repository Structure

The repository follows a topic-based organization:

- **AI/** - AI/ML notes including LLM tutorials, RAG implementations, PyTorch guides, and AI coding tools
- **Architecture/** - Software architecture patterns and system design
- **Blog/** - Technical blog posts and articles
- **Books/** - Book notes and summaries (2021 reading challenge, technical books)
- **Cloud Native/** - Cloud-native technologies, Docker, Kubernetes documentation and sample projects
- **Data-Processing/** - Big Data (HBase, Kafka, Flink), databases (MySQL, Redis), MyBatis
- **Datastructure&Algorithm/** - Algorithm implementations, LeetCode solutions, data structures
- **Domain Driven Design/** - DDD concepts and implementations
- **Elasticsearch/** - Elasticsearch core technology notes
- **English/** - English learning materials (grammar, vocabulary, The Economist articles)
- **Film/** - Film theory and appreciation notes
- **Go/** - Go programming language notes and examples
- **Java8/, JVM/, Java调优/** - Java ecosystem: language features, JVM internals, performance tuning
- **Microservices/** - Microservices architecture (Apollo, Zookeeper, OAuth2)
- **Multithreading/** - Concurrency and parallel programming
- **Netty/** - Netty framework notes and examples
- **Network/, OS/** - Computer networks and operating systems
- **PrepareForInterview/** - Interview preparation materials and system design
- **Python/** - Python programming notes
- **Quant/** - Quantitative finance materials
- **Shell/** - Shell scripting and Linux commands
- **Spring/** - Spring Framework (Core, Boot, Security, Batch)
- **TestAutomation/** - Testing and automation
- **Web3/** - Blockchain, Ethereum, stablecoins

## Obsidian Configuration

This repository uses Obsidian with the following key plugins:
- **obsidian-git** - Automatic git synchronization
- **dataview** - Data queries and dynamic views
- **obsidian-tasks-plugin** - Task management within notes
- **obsidian-excalidraw-plugin** - Diagram creation
- **calendar** - Calendar view for daily notes
- **asciidoctor-editor** - AsciiDoc support
- **obsidian-image-auto-upload-plugin** - Image upload automation

## Git Workflow

### Using git.sh Helper Script

The repository includes a `git.sh` script for simplified commits:

```bash
./git.sh <module-name>
# Automatically: adds changes, commits with "[module-name] update", pulls, and pushes
```

### Standard Git Commands

For more control over commits:

```bash
git add <path>
git commit -m "your message"
git pull
git push
```

## Working with Java Projects

The repository contains multiple Maven-based Java projects (61 pom.xml files):

**Build a specific project:**
```bash
cd <project-directory>
mvn clean install
```

**Run tests:**
```bash
mvn test
```

**Common project locations:**
- `Cloud Native/project/cloud-native-java/` - Microservices demo with Eureka
- `Data-Processing/Flink/frauddetection/` - Flink fraud detection example
- `Datastructure&Algorithm/algorithm-design-patterns/` - Algorithm implementations
- `Microservices/apollo-demo/` - Apollo configuration center demo
- `Spring/` - Various Spring framework examples

## Working with Python/AI Projects

Python projects primarily located in `AI/` directory:

**RAG Implementation Example:**
```bash
cd AI/LLM/RAG/RAG-in-action/chapter-1/llamaindex-in-action
# Setup virtual environment
python -m venv .venv
source .venv/bin/activate  # Windows: .venv\Scripts\activate
pip install -r requirements.txt
# Requires .env file with DEEPSEEK_API_KEY
python deepseek_rag.py
```

## Content Organization Patterns

### Markdown Notes Structure
Most directories follow this pattern:
- `/doc/` - Main documentation/notes in Markdown
- `/image/` or `/images/` - Supporting images and diagrams
- `/resources/` - Additional resources (PDFs, reference materials)
- Project folders - Actual code implementations

### File Naming
- Chinese file names are common and intentional
- Some files mix Chinese/English based on source material
- Key documents often include author/source in filename

## Key Technical Areas

### Deep Dive Topics with Code Examples
1. **Cloud Native/Microservices** - Spring Cloud, service discovery, config management
2. **Data Processing** - Kafka Streams, Flink streaming, HBase
3. **Algorithms** - Data structures, LeetCode solutions, algorithm patterns
4. **Spring Ecosystem** - Spring Boot, Security, Batch processing
5. **AI/ML** - RAG implementations, PyTorch tutorials, LLM applications

### Study Notes (Theory-focused)
1. **Books/** - Reading notes and summaries
2. **English/** - Language learning materials
3. **PrepareForInterview/** - Interview preparation guides

## Special Files

- **MCP is all your need.md** - Comprehensive guide on Model Context Protocol and LLM agents
- **Modern IM.md** - Deep dive into modern instant messaging system design
- **the-state-of-enterprise-ai_2025-report.pdf** - Enterprise AI report

## Search and Tool Usage Rules

**CRITICAL: Always use ENGLISH keywords when searching**

When using search tools (Glob, Grep, LSP, etc.), you MUST use English keywords, even when the repository contains Chinese content. This is essential for effective code and documentation discovery.

### Search Guidelines

1. **Use English keywords for all searches**
   - ✓ Correct: `Grep -pattern "authentication" -i`
   - ✗ Wrong: `Grep -pattern "认证" -i`
   - ✓ Correct: `Glob **/*test*.{ts,js}`
   - ✗ Wrong: `Glob **/*测试*.{ts,js}`

2. **Code searches must use technical English terms**
   - Class names, function names, variable names are typically in English
   - Technical concepts (API, database, cache, etc.) should be searched in English
   - Framework/library names (Spring, React, PyTorch) are always English

3. **File path searches use English**
   - Directory names in this repo use English (e.g., `AI/`, `Spring/`, `Data-Processing/`)
   - Search for `**/Spring/**/*.md` not `**/弹簧/**/*.md`

4. **Content searches can search for English even in mixed-language documents**
   - Documentation may be in Chinese, but technical terms, code samples, and API names are in English
   - Example: Search "authentication" to find auth-related content even in Chinese docs

### Examples

**Good Search Practices:**
```bash
# Find authentication-related files
Grep -pattern "authentication|auth|login" -i

# Find Spring configuration files
Glob **/Spring/**/*config*.{xml,yml,java}

# Find test files
Glob **/*test*.{ts,js,py}

# Find error handling code
Grep -pattern "try.*catch|error handling|exception" -i
```

**Avoid:**
```bash
# Don't search Chinese technical terms
Grep -pattern "认证|登录"  # This won't find code effectively

# Don't use Chinese in file patterns
Glob **/*测试*.java  # Won't match test files
```

### When Chinese Search is Acceptable

Only search Chinese when looking for:
- Chinese prose/documentation content (book notes, blog posts)
- Chinese comments in code (rare in this repo)
- Specific Chinese terminology that has no English equivalent

### Documentation Language Flexibility

- **Writing documentation**: Can be in Chinese, English, or bilingual - your choice based on context
- **Searching for content**: ALWAYS prefer English keywords
- **Code/variable naming**: Follow existing patterns (typically English)

## Important Notes

1. **Mixed Languages**: Content is in both Chinese and English - respect the original language when editing
2. **Learning Repository**: This is a personal knowledge base, not production code
3. **Git Plugin**: Obsidian-git plugin may auto-commit - coordinate with manual git operations
4. **Code Examples**: Many subdirectories contain standalone example projects with their own build systems
5. **Documentation First**: Most value is in markdown documentation rather than code
6. **Search in English**: Always use English keywords when searching (see "Search and Tool Usage Rules" above)
