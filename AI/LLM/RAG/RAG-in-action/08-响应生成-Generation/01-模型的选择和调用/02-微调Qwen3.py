from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    TrainingArguments,
    Trainer,
    DataCollatorForLanguageModeling
)
from datasets import load_dataset
import torch
import os

def prepare_dataset(tokenizer):
    # 加载测试数据集（这里使用一个简单的问答数据集作为示例）
    dataset = load_dataset("squad", split="train[:100]")  # 使用前100条数据作为示例
    
    def format_prompt(example):
        # 将SQuAD数据集格式化为对话格式
        prompt = f"问题：{example['question']}\n上下文：{example['context']}\n回答：{example['answers']['text'][0]}"
        return {"text": prompt}
    
    # 格式化数据集
    dataset = dataset.map(format_prompt)
    
    # 对数据集进行tokenize
    def tokenize_function(examples):
        return tokenizer(
            examples["text"],
            padding="max_length",
            truncation=True,
            max_length=512,
            return_tensors="pt"
        )
    
    tokenized_dataset = dataset.map(
        tokenize_function,
        batched=True,
        remove_columns=dataset.column_names
    )
    
    return tokenized_dataset

def main():
    # 设置模型名称和输出目录
    model_name = "Qwen/Qwen3-0.6B"
    output_dir = "./qwen3_finetuned"
    
    print("正在加载模型和tokenizer...")
    tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
    model = AutoModelForCausalLM.from_pretrained(
        model_name,
        device_map="auto",
        trust_remote_code=True
    )
    
    # 准备数据集
    print("正在准备数据集...")
    dataset = prepare_dataset(tokenizer)
    
    # 设置训练参数
    training_args = TrainingArguments(
        output_dir=output_dir,
        num_train_epochs=1,  # 训练轮数
        per_device_train_batch_size=4,  # 每个设备的batch大小
        gradient_accumulation_steps=4,  # 梯度累积步数
        learning_rate=2e-5,  # 学习率
        weight_decay=0.01,  # 权重衰减
        warmup_steps=100,  # 预热步数
        logging_steps=10,  # 日志记录步数
        save_steps=100,  # 保存检查点的步数
        fp16=True,  # 使用混合精度训练
    )
    
    # 创建数据整理器
    data_collator = DataCollatorForLanguageModeling(
        tokenizer=tokenizer,
        mlm=False  # 不使用掩码语言模型
    )
    
    # 创建训练器
    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=dataset,
        data_collator=data_collator,
    )
    
    # 开始训练
    print("开始训练...")
    trainer.train()
    
    # 保存模型
    print(f"训练完成，保存模型到 {output_dir}")
    trainer.save_model()
    tokenizer.save_pretrained(output_dir)
    
    # 测试微调后的模型
    print("\n测试微调后的模型...")
    test_prompt = "问题：什么是人工智能？\n回答："
    inputs = tokenizer(test_prompt, return_tensors="pt").to(model.device)
    outputs = model.generate(
        **inputs,
        max_new_tokens=100,
        do_sample=True,
        temperature=0.7,
        top_p=0.9
    )
    
    response = tokenizer.decode(outputs[0], skip_special_tokens=True)
    print("\n模型回答:", response)

if __name__ == "__main__":
    main()