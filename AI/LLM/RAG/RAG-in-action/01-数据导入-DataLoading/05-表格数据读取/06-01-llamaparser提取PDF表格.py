from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.core import Settings
from llama_parse import LlamaParse
import time
from dotenv import load_dotenv

# 加载环境变量（确保有OpenAI API密钥）
load_dotenv()

# 设置基础模型
embed_model = OpenAIEmbedding(model="text-embedding-3-small")
llm = OpenAI(model="gpt-3.5-turbo-0125")

Settings.llm = llm
Settings.embed_model = embed_model

# 定义PDF路径
pdf_path = "90-文档-Data/复杂PDF/billionaires_page-1-5.pdf"

# 记录开始时间
start_time = time.time()

# 使用LlamaParse解析PDF
documents = LlamaParse(result_type="markdown").load_data(pdf_path)

# 记录结束时间
end_time = time.time()
print(f"PDF解析耗时: {end_time - start_time:.2f}秒")

# 打印解析结果
print("\n解析后的文档内容:")
for i, doc in enumerate(documents, 1):
    print(f"\n文档 {i} 内容:")
    print(doc.text)
