"""
LLMLingua文本压缩示例
LLMLingua是一个用于压缩长文本的工具,可以在保持核心信息的同时减少token数量
适用于RAG系统中的检索后处理,降低LLM输入成本
"""

from llmlingua import PromptCompressor

# =============================================================================
# 1. 初始化PromptCompressor压缩器
# =============================================================================

# 强制使用CPU设备以避免CUDA错误
# model_name: 指定用于压缩的基础模型,这里使用Llama-2-7b
# device_map: 设备映射,"cpu"表示使用CPU运行,避免GPU内存不足问题
llm_lingua = PromptCompressor(
    model_name="NousResearch/Llama-2-7b-hf",
    device_map="cpu"  # 明确指定使用CPU
)

# =============================================================================
# 2. 文本压缩示例
# =============================================================================

# 压缩长文本内容
# context: 需要压缩的原始文本内容
# instruction: 压缩指令,告诉模型如何进行压缩
# question: 相关问题（可选）,帮助模型理解压缩重点
# target_token: 目标token数量,压缩后的文本应该控制在这个长度内
compressed_prompt = llm_lingua.compress_prompt(
    context="云冈石窟位于中国北部山西省大同市西郊17公里处的武周山南麓,石窟依山开凿,东西绵延1公里。存有主要洞窟45个,大小窟龛252个,石雕造像51000余躯,为中国规模最大的古代石窟群之一,与敦煌莫高窟、洛阳龙门石窟和天水麦积山石窟并称为中国四大石窟艺术宝库。 1961年被国务院公布为全国首批重点文物保护单位,2001年12月14日被联合国教科文组织列入世界遗产名录,2007年5月8日被国家旅游局评为首批国家5A级旅游景区。……",
    instruction="压缩并保持主要内容",
    question="",
    target_token=10  # 设定目标token数
)

# 输出压缩后的文本
print("=== 压缩后的文本 ===")
print(compressed_prompt)
print(compressed_prompt['compressed_prompt'])
print(f"压缩比例: {compressed_prompt.get('rate', 'N/A')}")

# =============================================================================
# 3. JSON数据压缩示例
# =============================================================================

# 定义要压缩的JSON数据
json_data = {
    "id": 987654,
    "name": "悟空",
    "biography": "孙悟空，法号行者，是中国古典神魔小说《西游记》中的主要角色之一。孙悟空由开天辟地以来的仙石孕育而生，因拜菩提祖师为师学会地煞数七十二变，又从龙宫索取如意金箍棒作为兵器，因大闹天宫被如来佛祖压在五行山下，无法行动。五百年后唐僧西天取经，路过五行山，揭去符咒，才救下孙悟空。孙悟空感激涕零，经观世音菩萨点拨，拜唐僧为师，同往西天取经。"
}

# JSON压缩配置
# rate: 压缩比例,0-1之间,越小压缩越严重
# compress: 是否对该字段进行压缩
# pair_remove: 是否允许移除键值对
# value_type: 值的类型（number/string等）
json_config = {
    "id": {"rate": 1, "compress": False, "pair_remove": False, "value_type": "number"},      # ID不压缩,保持原样
    "name": {"rate": 0.7, "compress": False, "pair_remove": False, "value_type": "string"}, # 姓名轻度压缩
    "biography": {"rate": 0.3, "compress": True, "pair_remove": False, "value_type": "string"}  # 传记重度压缩
}

# 执行JSON压缩 - 添加异常处理
try:
    print("\n=== 原始JSON数据 ===")
    import json
    print(json.dumps(json_data, ensure_ascii=False, indent=2))
    
    compressed_json = llm_lingua.compress_json(json_data, json_config)
    
    # 输出压缩后的JSON
    print("\n=== 压缩后的JSON ===")
    print(compressed_json['compressed_prompt'])
    
except Exception as e:
    print(f"\n=== JSON压缩出现错误 ===")
    print(f"错误类型: {type(e).__name__}")
    print(f"错误信息: {str(e)}")
    print("可能的解决方案:")
    print("1. 简化JSON中的文本内容")
    print("2. 避免使用特殊字符和转义字符")
    print("3. 调整压缩参数")
    
    # 尝试备用方案：只压缩文本内容
    print("\n=== 使用备用文本压缩方案 ===")
    try:
        backup_compressed = llm_lingua.compress_prompt(
            context=json_data["biography"],
            instruction="压缩传记内容，保持关键信息",
            question="",
            target_token=30
        )
        print("压缩后的传记:")
        print(backup_compressed['compressed_prompt'])
    except Exception as backup_e:
        print(f"备用方案也失败: {backup_e}")

# =============================================================================
# 使用说明
# =============================================================================
"""
压缩器的主要应用场景：
1. RAG系统中压缩检索到的文档,减少输入token
2. 压缩历史对话记录,保持上下文连贯性
3. 压缩结构化数据（JSON）,保留关键字段

参数调优建议：
- target_token: 根据下游LLM的上下文限制设置
- rate: 根据信息重要性调整,关键信息设置较高值
- compress: 数值型数据通常不需要压缩
"""

