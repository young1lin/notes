from transformers import AutoModelForCausalLM, AutoTokenizer
import torch

def main():
    # 加载Qwen3小模型和对应的tokenizer
    model_name = "Qwen/Qwen3-0.6B"  # Qwen3的小模型版本
    
    print("正在加载模型和tokenizer...")
    tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
    model = AutoModelForCausalLM.from_pretrained(
        model_name,
        device_map="auto",
        trust_remote_code=True
    ).eval()
    
    # 设置对话提示词
    prompt = "你好，请介绍一下你自己。"
    
    # 生成回答
    print("\n用户输入:", prompt)
    inputs = tokenizer(prompt, return_tensors="pt").to(model.device)
    outputs = model.generate(
        **inputs,
        max_new_tokens=200,
        do_sample=True,
        temperature=0.7,
        top_p=0.9
    )
    
    response = tokenizer.decode(outputs[0], skip_special_tokens=True)
    print("\n模型回答:", response)

if __name__ == "__main__":
    main()