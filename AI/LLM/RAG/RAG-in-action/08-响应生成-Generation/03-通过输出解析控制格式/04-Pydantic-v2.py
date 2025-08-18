from pydantic import BaseModel, Field
from typing import List, Optional
from llama_index.program.openai import OpenAIPydanticProgram

# 定义代码问题模型
class CodeIssue(BaseModel):
    """代码中存在的问题"""
    line_number: int = Field(..., description="问题所在的行号")
    issue_type: str = Field(..., description="问题类型，如：安全漏洞、性能问题、代码风格等")
    description: str = Field(..., description="问题的详细描述")
    severity: str = Field(..., description="问题严重程度：high/medium/low")

# 定义代码分析报告模型
class CodeAnalysis(BaseModel):
    """代码分析报告"""
    file_name: str = Field(..., description="被分析的文件名")
    issues: List[CodeIssue] = Field(default_factory=list, description="发现的问题列表")
    overall_quality: str = Field(..., description="代码整体质量评估：excellent/good/fair/poor")
    recommendations: List[str] = Field(default_factory=list, description="改进建议")

# 创建 OpenAI Pydantic Program
program = OpenAIPydanticProgram.from_defaults(
    output_cls=CodeAnalysis,
    prompt_template_str="""
请分析以下代码，生成详细的分析报告：
{code}

要求：
1. 识别代码中的潜在问题
2. 评估代码质量
3. 提供改进建议
""",
    verbose=True
)

# 示例代码
sample_code = """
def process_data(data):
    if data is None:
        return
    for item in data:
        if item > 100:
            print("Large value found")
        else:
            print("Small value")
"""

# 运行分析
try:
    analysis = program(code=sample_code)
    
    print(f"文件分析报告: {analysis.file_name}")
    print(f"整体质量: {analysis.overall_quality}")
    
    print("\n发现的问题:")
    for issue in analysis.issues:
        print(f"- 行号 {issue.line_number}: {issue.issue_type}")
        print(f"  描述: {issue.description}")
        print(f"  严重程度: {issue.severity}")
    
    print("\n改进建议:")
    for rec in analysis.recommendations:
        print(f"- {rec}")
        
except Exception as e:
    print(f"分析过程中出现错误: {e}")