# Anthropic 和 OpenAI 的 2025 年 AI 编程报告全文翻译（中英双语版）

> 本文档包含 Anthropic 和 OpenAI 在 2025 年发布的关于 AI 辅助编程和企业 AI 应用的官方研究报告的完整翻译。
> 采用"英文原文 + 中文翻译"的双语对照格式，便于深度阅读和理解。

---

## 一、Anthropic 报告："AI 如何改变 Anthropic 的工作方式"

**原文标题**: How AI is Transforming Work at Anthropic
**发布时间**: 2025 年 8 月
**报告来源**: [https://www.anthropic.com/research/how-ai-is-transforming-work-at-anthropic](https://www.anthropic.com/research/how-ai-is-transforming-work-at-anthropic)

---

### [ENGLISH ORIGINAL] Introduction

**How is AI changing the way we work?**

Anthropic's previous research on AI's economic impacts examined the labor market broadly across various job categories. However, this study turns inward to examine early AI adopters within the organization itself.

In August 2025, Anthropic surveyed 132 engineers and researchers, conducted 53 in-depth qualitative interviews, and analyzed internal Claude Code usage data spanning 200,000 transcripts from February and August 2025. The research reveals that AI is radically transforming software development work, generating both opportunities and concerns.

---

### [中文翻译] 研究概述

AI 如何改变我们的工作方式？

Anthropic 之前关于 AI 经济影响的研究广泛考察了各个职业类别的劳动力市场。然而，本研究转向内部，考察组织内部的早期 AI 采用者。

Anthropic 在 2025 年 8 月开展了一项全面研究，考察 Claude 的使用如何改变软件开发工作。该研究包括：

- **132 名员工调查**
- **53 次深度访谈**
- **分析 20 万条 Claude Code 对话记录**（来自 2025 年 2 月和 8 月）

这是一次内部的、自我审视的研究，旨在理解 AI 工具如何在实际工作中被使用，以及它对工作方式、技能发展和职业轨迹的影响。

---

### [ENGLISH ORIGINAL] Key Research Findings - Survey Data Results

**Task Usage Patterns:**
- 55% of employees use Claude daily for debugging
- 42% use Claude daily for code understanding
- 37% use Claude daily for implementing new features
- Less frequent uses include high-level design/planning, data science, and front-end development

**Productivity Metrics:**
- Claude usage increased from 28% to 59% of daily work year-over-year
- Self-reported productivity gains grew from +20% to +50%
- This represents a 2-3x increase in both metrics within one year
- 14% of respondents report productivity boosts exceeding 100%

**Work Composition:**
- 27% of Claude-assisted work consists of tasks that wouldn't otherwise be completed
- Engineers cite scaling projects, nice-to-have tools, and exploratory work as examples
- Most employees report they can only "fully delegate" 0-20% of their work to Claude
- Active supervision and validation remain standard practice, particularly for complex or high-stakes work

---

### [中文翻译] 核心发现

#### 1. 生产力提升

**使用频率的飞跃**：
- 员工报告在 **60% 的日常工作**中使用 Claude（一年前为 28%）
- 自报生产力提升：**50%**（之前为 20%）
- 这代表一年内两个指标都增长了 **2-3 倍**
- **14% 的受访者**报告生产力提升超过 **100%**
- **27% 的 Claude 辅助工作**是"原本不会完成的任务"

**关键洞察**：这 27% 不是"必须完成的核心工作"，而是"nice-to-have"的边缘任务——扩展项目、制作实用工具、优化流程等。AI 让专家有余力去做以前"没时间做"的事情。

#### 2. 使用模式：不是写代码，是理解代码

**每日使用统计**：

| 用途 | 每日使用比例 |
|------|-------------|
| **调试（Debugging）** | **55%** |
| **代码理解（Code Understanding）** | **42%** |
| 实现新功能（Feature Implementation） | 37% |
| 高级设计/规划 | 较少使用 |
| 数据科学 | 较少使用 |
| 前端开发 | 较少使用 |

**最重要的发现**：最常见的用途不是"生成代码"，而是"理解和修复代码"。

**但是**，大多数员工报告只能"完全委托"给 AI 的工作量仅为 **0-20%**。这意味着：
- AI 不是在"替代"工作
- 而是在"辅助"工作
- 人类仍然需要深度参与和验证
- 对于复杂或高风险的工作，主动监督和验证仍是标准实践

#### 3. 技能转变：成为"更全栈"的工程师

**正面效应**：

工程师们描述自己变得"更加全栈"（more full-stack），能够处理：
- 前端开发（Frontend）
- 事务性数据库（Transactional Databases）
- 其他不熟悉的领域

> "我现在能够非常熟练地处理前端，或者事务性数据库，而以前我会害怕触碰这些东西。"

**但也有担忧**：

一些人表达了对**技能退化**的担忧：

> "当产出如此容易和快速时，真正花时间去学习某件事就变得更难了。"

**监督悖论**：

> "我更担心的是监督和监控问题……我的技能退化主要是关于我安全使用 AI 的能力的问题。"

**矛盾的悖论**：AI 让你能做更多事情，但也可能让你失去深度做某件事的能力。

---

### [ENGLISH ORIGINAL] Interview Insights

**AI Delegation Strategies:**

Engineers develop systematic approaches for determining what to delegate:

- Tasks outside their expertise but low complexity
- Work that is easily verifiable before deployment
- Well-defined or self-contained components
- Code where quality standards are less critical
- Repetitive or tedious assignments
- Tasks requiring less than 10 minutes of implementation time

One engineer noted: **"The more excited I am to do the task, the more likely I am to not use Claude."**

**Trust Progression:**

Many engineers describe gradually increasing their Claude usage over time. One comparison: "In the beginning I would use [Google Maps] only for routes I didn't know... Today I use Google Maps all the time, even for my daily commute."

Engineers remain divided on whether to use Claude for familiar versus unfamiliar domains. Some prefer using it for peripheral areas to save implementation time, while others prefer familiar territory where they can verify outputs.

**Skill Transformations:**

The research reveals a paradox in skill development:

*Expansion:* Engineers report becoming "more full-stack" and capable in domains previously avoided. "I can very capably work on front-end, or transactional databases, where previously I would've been scared to touch stuff."

*Atrophy Concerns:* Simultaneously, some worry about losing hands-on proficiency. "When producing output is so easy and fast, it gets harder to actually take the time to learn something."

One engineer highlighted the supervision paradox: "I worry much more about the oversight and supervision problem... having my skills atrophy is primarily problematic with respect to my ability to safely use AI."

**Changing Work Relationships:**

Several themes emerged regarding workplace dynamics:

- Claude has become the "first stop for questions" previously directed to colleagues
- About half report unchanged team collaboration patterns
- Others describe significantly reduced colleague interaction: "I work way more with Claude than with any of my colleagues"
- Traditional mentorship dynamics are shifting as Claude provides coaching to junior staff
- One senior engineer observed: "It's been sad that more junior people don't come to me with questions as often"

**The Craft of Coding:**

Engineers express divergent perspectives on meaning and satisfaction:

Some experience loss: "It's the end of an era for me - I've been programming for 25 years, and feeling competent in that skill set is a core part of my professional satisfaction."

Others embrace the shift: "I thought that I really enjoyed writing code, and I think instead I actually just enjoy what I get out of writing code."

One pragmatist noted: "I am for sure atrophying in my skills as a software engineer... But those skills could come back if they ever needed to, and I just don't need them anymore!"

**Career Uncertainty:**

Long-term trajectory concerns dominate many conversations:

"I feel optimistic in the short term but in the long term I think AI will end up doing everything and make me and many others irrelevant."

Engineers increasingly see themselves as "managers of AI agents," with work shifting "70%+ to being a code reviewer/reviser rather than a net-new code writer."

Many emphasize genuine uncertainty: "I have very low confidence in what specific skills I think will be useful in the future."

---

### [中文翻译] 定性洞察：员工如何使用 AI

#### 4. AI 任务委托策略：什么任务适合给 AI？

工程师们开发了系统性的方法来决定委托什么任务：

| 任务特征 | 说明 | 示例 |
|---------|------|------|
| **低复杂度，但超出专业领域** | 不难，但我不熟悉 | 前端工程师用 AI 写 SQL 查询 |
| **容易验证** | 我能快速判断对错 | 单元测试、数据转换脚本 |
| **定义明确或独立** | 需求清晰，不依赖上下文 | 格式化函数、配置文件生成 |
| **质量标准不严格** | 对代码质量要求较低的地方 | 临时脚本、工具代码 |
| **重复性或低风险** | 做错了影响不大 | 日志记录、注释生成 |
| **提示比手动执行更快** | 描述需求比写代码快 | 正则表达式、Bash 脚本 |
| **实现时间少于 10 分钟的任务** | 小型快速任务 | 简单的辅助函数 |

**关键引用**：

> "我对任务越兴奋，就越不太可能使用 Claude。"
> —— 一位 Anthropic 工程师

**解读**：人们把"不想做的事"交给 AI，把"想做的事"留给自己。AI 是效率工具，不是替代创造力的工具。

**信任进展**：

许多工程师描述逐渐增加 Claude 使用的过程。一个类比：

> "一开始我只会在不知道路线的时候使用[谷歌地图]……今天我一直在使用谷歌地图，即使是日常通勤。"

工程师们对是否在熟悉与不熟悉的领域使用 Claude 存在分歧：
- 有些人更喜欢在外围领域使用它以节省实现时间
- 其他人更喜欢在熟悉的领域使用，因为他们可以验证输出

#### 5. 工作场所动态：Claude 成为"第一求助对象"

**变化前**：遇到问题 → 问同事
**变化后**：遇到问题 → 先问 Claude → 如果解决不了再问同事

**影响**：

**正面**：
- 减少社交摩擦（不用担心打扰别人）
- 即时响应（不用等同事有空）
- 大约一半的人报告团队协作模式没有改变

**负面**：
- **减少导师机会**：初级工程师少了向资深同事学习的机会
- **显著减少同事互动**：一些人描述"我和 Claude 一起工作的时间远多于和任何同事"
- **知识传递受阻**：团队知识可能更多地存储在 AI 对话中，而不是人与人的交流中
- **传统导师动态正在转变**：Claude 为初级员工提供指导

资深工程师观察：
> "很遗憾，初级员工不像以前那样经常来问我问题了。"

#### 6. 编程的工艺性：意义与满足感

工程师们对意义和满足感表达了不同的观点：

**体验失落**：
> "对我来说，这是一个时代的终结——我已经编程 25 年了，在这个技能集上感到胜任是我职业满足感的核心部分。"

**拥抱转变**：
> "我以为我真的很享受写代码，但我想我实际上只是享受从写代码中得到的东西。"

**实用主义者**：
> "我的软件工程师技能肯定在退化……但如果需要的话，这些技能可以恢复，而我现在根本不需要它们！"

#### 7. 职业不确定性：长期焦虑

员工表达了复杂的情绪：

**短期乐观 vs 长期焦虑**：

> "我对短期感到乐观，但从长远来看，我认为 AI 最终会做所有事情，让我和许多其他人变得无关紧要。"

**角色转变**：

工程师们越来越多地将自己视为"AI 代理的管理者"，工作转变为"70% 以上是代码审查者/修订者，而不是全新代码编写者"。

**真实的不确定性**：

> "我对未来哪些具体技能会有用非常没有信心。"

**这种矛盾情绪很真实**：
- 现在：AI 让我更高效
- 未来：AI 会让我的职业消失吗？

---

### [ENGLISH ORIGINAL] Claude Code Usage Trends

**Increasing Autonomy**

Between February and August 2025, significant changes emerged in Claude Code usage:

**Task Complexity:**
- Average task complexity increased from 3.2 to 3.8 on a 1-5 scale
- Examples: Tasks shifted from "Troubleshoot Python module import errors" to "Implement and optimize caching systems"

**Autonomous Operations:**
- Maximum consecutive tool calls increased 116% (from 9.8 to 21.2 actions)
- Human intervention turns decreased 33% (from 6.2 to 4.1 per transcript)
- Claude requires less frequent human steering for complex workflow completion

**Task Distribution Evolution:**

Feature implementation usage expanded dramatically:
- Increased from 14.3% to 36.9% of Claude Code tasks
- Code design/planning grew from 1.0% to 9.9%

**Papercut Fixes:**

8.6% of Claude Code tasks involve minor quality-of-life improvements previously deprioritized:
- Code refactoring for maintainability
- Creating performance visualization tools
- Building terminal shortcuts

These small fixes cumulatively improve workflow efficiency and reduce friction.

**Team-Specific Patterns:**

**Pre-training Team:**
- Uses Claude Code for building new features in 54.6% of tasks
- Primarily running additional experiments

**Alignment & Safety and Post-training Teams:**
- Perform most front-end development (7.5% and 7.4% respectively)
- Create data visualizations for research

**Security Team:**
- Uses Claude Code for code understanding 48.9% of the time
- Focuses on analyzing security implications

**Non-technical Employees:**
- Debug at 51.5% of Claude Code tasks
- Use Claude for data science (12.7%)
- Claude bridges technical knowledge gaps

---

### [中文翻译] 技术进展：Claude Code 的能力增长

#### 8. 自主性增强

**六个月的进步**（2025 年 2 月至 8 月）：

| 指标 | 6 个月前（2月） | 现在（8月） | 增长 |
|------|---------|---------------------|------|
| **任务复杂度评分**（1-5 分） | 3.2 | 3.8 | **+18.75%** |
| **连续自主动作数** | ~10 (9.8) | ~20 (21.2) | **+116%（2倍多）** |
| **需要人工干预的频率** | 6.2次/对话 | 4.1次/对话 | **-33%** |

**任务复杂度示例**：
- **之前**："排查 Python 模块导入错误"
- **现在**："实现和优化缓存系统"

**解读**：
- Claude Code 不仅能做更多事，还能做更复杂的事
- Claude 在完成复杂工作流时需要的人工引导更少
- 但仍然需要人工监督——只是频率降低了

#### 9. 任务类型的演变

**功能实现（Feature Implementation）的崛起**：

| 时间 | 功能实现占比 | 代码设计/规划占比 |
|------|-------------|------------------|
| 6 个月前（2025 年 2 月） | 14.3% | 1.0% |
| 现在（2025 年 8 月） | **36.9%** | **9.9%** |

**新任务类型 - "Papercut fixes"（小问题修复）**：

占 **8.6%** 的 Claude Code 任务：
- 为可维护性重构代码
- 创建性能可视化工具
- 构建终端快捷方式
- 指那些"一直想修但从来没时间修"的小 bug 或体验问题
- AI 让这些"长尾任务"终于有了被解决的机会
- 这些小修复累积起来显著改善工作流效率并减少摩擦

**趋势**：从"帮我理解这段代码"到"帮我实现这个功能"——AI 正在承担更多创造性工作。

---

### [中文翻译] 团队特定模式：不同团队如何使用 Claude

#### 10. 按团队分析使用模式

**Pre-training 团队（预训练团队）**：
- **功能实现**占 **54.6%** 的任务
- 理由：训练模型的工具开发需求很大
- 主要运行额外的实验

**Alignment & Safety 和 Post-training 团队**：
- 执行最多的**前端开发**（分别为 7.5% 和 7.4%）
- 为研究创建数据可视化
- 理由：研究人员用 AI 快速构建实验界面

**Security 团队（安全团队）**：
- **代码理解**占 **48.9%** 的时间
- 理由：安全审计需要深入理解陌生代码
- 专注于分析安全影响

**非技术员工**：
- **调试**：51.5% 的 Claude Code 任务
- **数据科学**：12.7%
- 理由：非技术人员用 AI 完成以前需要工程师帮忙的任务
- Claude 弥合了技术知识鸿沟

**关键洞察**：AI 不是"一刀切"的工具，不同专业背景的团队使用方式差异巨大。

---

### [ENGLISH ORIGINAL] Limitations and Caveats

**Selection Bias:**

Respondents likely skewed toward those particularly engaged with Claude or holding strong opinions. Those with neutral experiences may be underrepresented.

**Response Bias:**

Responses may reflect social desirability bias, as participation was non-anonymous and all respondents were Anthropic employees. Recency bias affected recall of productivity and usage patterns from 12 months prior.

**Productivity Measurement Challenges:**

Self-reported productivity metrics are difficult to validate precisely. External research from METR suggested experienced developers on familiar codebases overestimate AI productivity gains. However, Anthropic employees may have developed superior "strategic AI delegation skills" not captured in comparable studies.

The research notes ambiguity regarding where time savings are reinvested—whether toward additional engineering tasks, non-engineering work, Claude interaction and review, or activities outside work. Task categorization frameworks don't capture all possible time allocation patterns.

**Claude Code Analysis Limitations:**

Proportionate sampling across time periods allows measurement of relative task distribution changes only, not absolute changes in total work volume. When reporting feature implementation increased from 14% to 37%, this indicates relative proportion changes, not necessarily increased absolute feature work.

**Model Capability Context:**

Data collection occurred when Claude Sonnet 4 and Claude Opus 4 represented state-of-the-art capabilities. Subsequent model advances may have shifted observed patterns.

---

### [中文翻译] 方法论局限性：研究的诚实披露

#### 11. Anthropic 承认的研究偏差

研究人员坦诚地列出了多项局限性：

**1. 选择偏差（Selection Bias）**：
- 更积极使用 AI 的员工更可能参与调查
- 持有强烈观点的员工更可能参与
- 中立体验的员工可能代表性不足
- 可能高估了整体使用率和满意度

**2. 响应偏差（Response Bias）**：
- 社会期望偏差（Social Desirability Bias）：
  - 调查不匿名
  - 所有受访者都是 Anthropic 员工
  - 员工可能倾向于给出"公司希望听到的答案"
- 近期偏差（Recency Bias）：
  - 影响了对 12 个月前生产力和使用模式的回忆

**3. 生产力测量挑战**：
- "我觉得我效率提升了 50%"是主观感受
- 自报生产力指标难以精确验证
- 缺乏客观的代码质量、交付时间等指标验证
- METR 外部研究表明：
  - 经验丰富的开发者在熟悉的代码库上可能**高估** AI 生产力收益
  - 但是，Anthropic 员工可能已经开发出优越的"战略性 AI 委托技能"，这在可比研究中未被捕捉到

**时间节省再投资的模糊性**：
- 时间节省被再投资到哪里？
  - 额外的工程任务？
  - 非工程工作？
  - Claude 交互和审查？
  - 工作之外的活动？
- 任务分类框架无法捕捉所有可能的时间分配模式

**4. Claude Code 分析局限性**：
- **比例采样限制**：
  - 跨时间段的比例采样只能测量**相对任务分布变化**
  - **不能测量**总工作量的绝对变化
- **数据解读注意事项**：
  - 当报告"功能实现从 14% 增加到 37%"时
  - 这表示**相对比例变化**
  - **不一定意味着**绝对功能工作增加了

**5. 样本的特权地位**：
- Anthropic 员工是"最早采用者"
- 他们是 AI 工具的构建者，使用水平远超普通用户
- **这个研究可能代表的是"专家使用 AI"的最佳案例，而非普通用户的真实体验**

**6. 模型能力上下文**：
- 数据收集发生在 Claude Sonnet 4 和 Claude Opus 4 代表最先进能力的时候
- 随后的模型进步可能已经改变了观察到的模式
- 数据收集于 2025 年 8 月
- 但 Claude 的最新能力进展可能还没反映在数据中

**为什么这很重要**：
- Anthropic 的科学诚实值得尊重
- 这些局限性提醒我们：不要盲目推广这些发现到所有场景
- **专家在理想环境中的体验 ≠ 普通用户在真实世界的体验**

---

### [ENGLISH ORIGINAL] Looking Forward

**Internal Initiatives:**

Anthropic is pursuing several exploratory directions:

- Examining how teams can collaborate more effectively
- Supporting professional development in an AI-augmented context
- Establishing best practices for AI-integrated work guided by their AI fluency framework
- Expanding research beyond engineers to understand transformation across organizational roles
- Supporting external organizations like CodePath as they adapt computer science curricula

**Structural Considerations:**

The organization is considering:

- New pathways for role evolution and reskilling
- Approaches to maintain technical expertise amid AI delegation
- Methods to preserve meaningful colleague collaboration
- Learning and mentorship models suited to AI-augmented work

Anthropic frames itself as "a laboratory for responsible workplace transition," aiming to study and experiment with thoughtful navigation of AI's impact.

**Key Takeaway:**

More capable AI delivers clear productivity benefits while raising critical questions about technical expertise maintenance, meaningful human collaboration, and preparation for uncertain futures requiring new approaches to learning, mentorship, and career development. **The organization expects to share concrete plans in 2026 as thinking matures.**

---

### [中文翻译] 展望未来：Anthropic 的内部探索

#### 12. 公司正在探索的方向

Anthropic 正在内部探索以下方法：

**1. 团队协作机制**：
- 如何让团队更有效地协作
- 在 AI 成为"第一求助对象"的时代，维持团队知识传递

**2. 保持技术专业知识的发展**：
- 在 AI 增强的环境中支持职业发展
- 如何在 AI 辅助下，仍然培养工程师的核心技能？
- 在 AI 委托中保持技术专业知识的方法
- 是否需要"无 AI 时间"来练习基础能力？

**3. 建立 AI 集成工作的最佳实践**：
- 由他们的 AI 流畅度框架指导
- 什么任务应该委托给 AI？
- 什么任务必须人工完成？
- 如何验证 AI 输出的质量？

**4. 扩展研究范围**：
- 将研究扩展到工程师之外
- 理解整个组织角色的转变

**5. 支持外部组织**：
- 支持像 CodePath 这样的外部组织
- 帮助他们调整计算机科学课程

**6. 结构性考虑**：
- 角色演变和技能重塑的新路径
- 保留有意义的同事协作的方法
- 适合 AI 增强工作的学习和导师制度模型
- 导师制度如何适应新环境？
- 工程师的成长路径如何重新定义？
- 高级工程师的价值是什么——写代码的速度，还是架构判断力？

**Anthropic 的自我定位**：

> Anthropic 将自己定位为"负责任的工作场所转型实验室"（a laboratory for responsible workplace transition），旨在研究和实验如何深思熟虑地应对 AI 的影响。

**未来计划**：

> **计划在 2026 年随着思考的成熟分享具体计划。**

**关键信号**：即使是 AI 公司自己，也在摸索"如何正确使用 AI"的答案。

**核心要点**：

更强大的 AI 带来明确的生产力收益，同时提出了关于技术专业知识维护、有意义的人类协作以及为不确定的未来做准备的关键问题，这需要新的学习、导师制和职业发展方法。

---

### Anthropic 报告的核心结论

#### 关键要点总结

**1. AI 是放大器，不是替代品**：
- 员工在 60% 的工作中使用 Claude
- 但只能"完全委托" 0-20% 的工作
- **结论**：AI 辅助人类，而非取代人类

**2. 专家变得更专家**：
- 任务复杂度从 3.2 增长到 3.8
- 连续自主动作增加 116%
- 工程师变得"更全栈"
- **结论**：AI 拓展了专家的边界，而不是降低了门槛

**3. 最大价值是理解和调试，而非生成**：
- 55% 用于调试，42% 用于代码理解
- 功能实现占 37%
- **结论**：AI 帮助你理解现有系统，而非从零构建新系统

**4. 新的工作类型涌现**：
- 27% 的工作是"原本不会做的任务"
- 8.6% 是"Papercut fixes"
- **结论**：AI 让"长尾任务"变得可行

**5. 职业焦虑是真实的**：
- 短期乐观 vs 长期焦虑
- 技能退化的担忧
- 监督悖论
- **结论**：即使在 AI 公司内部，员工也在思考未来

**6. 研究的诚实性**：
- Anthropic 坦诚列出所有偏差和局限性
- 承认这可能代表"专家使用 AI"的最佳案例
- **结论**：这不是营销材料，而是真实的科学研究

---

### 对专家优势论的验证

Anthropic 的报告完美验证了"专家优势论"的核心观点：

| 专家优势论观点 | Anthropic 数据支持 |
|---------------|-------------------|
| **专家用 AI 更高效** | 任务复杂度从 3.2 增至 3.8，员工变得"更全栈" |
| **AI 需要人类监督** | 只能"完全委托" 0-20% 的工作 |
| **验证能力是关键** | 55% 用于调试，42% 用于代码理解——都需要深厚专业知识 |
| **AI 不是万能钥匙** | 员工选择性地使用 AI，"越兴奋的任务越不用 AI" |
| **技能发展仍然重要** | Anthropic 自己也在探索如何"保持技术专业知识的发展" |

**最重要的发现**：即使是 Anthropic 的工程师——世界上最懂 AI 的一群人——也只能将 0-20% 的工作"完全委托"给 AI。**这说明 AI 远未到"取代"人类的程度，而是"放大"人类的能力。**

---

## 二、OpenAI 报告："企业 AI 现状 2025"

**原文标题**: The State of Enterprise AI 2025
**发布时间**: 2025 年
**报告来源**:
- 网页版：[https://openai.com/index/the-state-of-enterprise-ai-2025-report/](https://openai.com/index/the-state-of-enterprise-ai-2025-report/)
- PDF 版：[https://cdn.openai.com/pdf/7ef17d82-96bf-4dd1-9df2-228f7f377a29/the-state-of-enterprise-ai_2025-report.pdf](https://cdn.openai.com/pdf/7ef17d82-96bf-4dd1-9df2-228f7f377a29/the-state-of-enterprise-ai_2025-report.pdf)

---

### [ENGLISH ORIGINAL] Foreword

**At OpenAI, our mission is to ensure that artificial intelligence benefits all of humanity, and helping enterprises solve problems is central to this mission.**

The majority of economically valuable activity takes place inside organizations, where innovation translates directly into improved outcomes for workers, customers, and other stakeholders. Enterprise problems also present the hardest technical challenges for frontier intelligence, requiring reliability, safety, and security at scale. The revenue generated from solving these problems can help fund broad, free access to powerful AI for hundreds of millions of people worldwide.

For much of the past three years, the visible impact of AI has been most apparent among consumers. However, the history of general purpose technologies—from steam engines to semiconductors—shows that significant economic value is created after firms translate underlying capabilities into scaled use cases. Enterprise AI now appears to be entering this phase, as many of the world's largest and most complex organizations are starting to use AI as core infrastructure.

More than 1 million business customers now use OpenAI's tools. This report brings together evidence from de-identified and aggregated enterprise usage data and a variety of other sources to provide a grounded view of how AI is being deployed inside organizations today.

**Ronnie Chatterji**
Chief Economist
OpenAI

---

### [中文翻译] 前言

**OpenAI 的使命**：

> "在 OpenAI，我们的使命是确保人工智能造福全人类，而帮助企业解决问题是这一使命的核心。"
> —— Ronnie Chatterji, OpenAI 首席经济学家

**为什么企业很重要**：

- 大多数经济价值活动发生在组织内部
- 创新直接转化为工人、客户和其他利益相关者的成果改善
- 企业问题对前沿智能提出最严苛的技术挑战：可靠性、安全性、大规模部署
- 解决这些问题产生的收入可以资助免费 AI 服务，惠及全球数亿人

**企业 AI 的转折点**：

过去三年，AI 的可见影响主要体现在消费者端。但从蒸汽机到半导体的历史表明，**重大经济价值是在企业将底层能力转化为规模化用例后创造的**。

企业 AI 现在似乎正在进入这一阶段，世界上许多最大、最复杂的组织开始将 AI 作为核心基础设施使用。

**数据规模**：

超过 **100 万企业客户**现在使用 OpenAI 的工具。本报告汇集了去识别化和聚合的企业使用数据以及各种其他来源的证据，提供了 AI 如何在组织内部部署的真实视角。

---

### [ENGLISH ORIGINAL] Four Key Findings Stand Out

**01. Enterprise usage is scaling, with deeper workflow integration.** ChatGPT message volume grew 8x and API reasoning token consumption per organization increased 320x year-over-year, demonstrating that more enterprises are using AI and their intensity of usage has increased.

**02. Enterprises that leverage AI are experiencing measurable productivity and business impact.** Enterprise users report saving 40–60 minutes per day and being able to complete new technical tasks such as data analysis and coding. Case studies indicate AI is contributing to important outcomes such as revenue growth, improved customer experience, and shorter product-development cycles.

**03. Enterprise growth is global and rapidly accelerating across industries.** Over the past six months, international adoption has surged as organizations worldwide deepen their use of AI, complementing continued strong momentum in the U.S. In the past 12 months, the median sector grew by more than 6x, with the technology sector leading the pack at 11x.

**04. A widening gap is emerging between leaders and laggards.** Frontier workers are sending 6x more messages and frontier firms are sending 2x as many messages per seat than the median enterprise. There's a substantive gap in the likelihood to utilize the most capable AI tools today, despite broad availability of these tools. Models are capable of far more than most organizations have embedded into workflows, and this presents an opportunity for firms.

Looking ahead, the next phase of enterprise AI will be shaped by stronger performance on economically valuable tasks, better understanding of organizational context, and a shift from asking models for outputs to delegating complex, multi-step workflows. As these capabilities mature, we expect organizations to not only improve efficiency, but discover new ways to serve customers and deliver value.

The findings in this report represent early signs of how AI is beginning to reshape the modern enterprise. As enterprise AI evolves, OpenAI will continue to share real-world evidence on how AI is influencing firms, workers, and the broader economy.

---

### [中文翻译] 四大核心发现

报告开篇就列出了四大关键发现：

**01. 企业使用正在扩展，工作流集成更深入**

ChatGPT 消息量增长 **8 倍**，每个组织的 API 推理 token 消耗增长 **320 倍**（同比），表明更多企业在使用 AI，使用强度也在增加。

**02. 使用 AI 的企业正在经历可测量的生产力和商业影响**

企业用户报告每天节省 **40-60 分钟**，能够完成新的技术任务（如数据分析和编程）。案例研究表明 AI 正在贡献重要成果：收入增长、客户体验改善、产品开发周期缩短。

**03. 企业增长是全球性的，跨行业快速加速**

过去 6 个月，国际采用激增，全球组织加深了对 AI 的使用，补充了美国持续强劲的势头。过去 12 个月，中位数行业增长超过 **6 倍**，技术行业领先，达到 **11 倍**。

**04. 领先者和落后者之间的差距正在扩大**

前沿员工发送的消息是中位数的 **6 倍**，前沿企业每席位发送的消息是中位数企业的 **2 倍**。尽管这些工具广泛可用，但使用最强大 AI 工具的可能性存在实质性差距。模型的能力远超大多数组织已嵌入到工作流中的水平，这为企业提供了机会。

**未来展望**：

下一阶段的企业 AI 将由以下因素塑造：
1. **更强的任务性能**：在经济上有价值的任务上表现更强
2. **更好的组织上下文理解**：更深入地理解组织特定的上下文
3. **从输出到委派的转变**：从要求模型提供输出转变为委派复杂的多步骤工作流程

随着这些能力的成熟，我们预计组织不仅会提高效率，还会发现服务客户和创造价值的新方式。

本报告中的发现代表了 AI 如何开始重塑现代企业的早期迹象。随着企业 AI 的发展，OpenAI 将继续分享关于 AI 如何影响企业、员工和更广泛经济的真实证据。

---

### [ENGLISH ORIGINAL] Introduction & Methodology

Over the past three years, enterprises have integrated AI systems across a wide range of use cases and operational workflows.

These deployments provide insights on how AI is shaping work, particularly in environments where accuracy standards are high, workflows are complex, and improvements in productivity or decision quality have direct economic outcomes. Because much of the world's economically valuable activity occurs inside firms, enterprise adoption patterns provide a clear signal of where AI is delivering value today and where it will likely do so in the future.

The scale and diversity of OpenAI's more than 1 million business customers provides a distinctive view into this shift. This report summarizes key findings from across OpenAI's enterprise customer base, and what those patterns suggest about the current state and trajectory of enterprise AI. By examining how adoption varies across industries and functions, the analysis also highlights where AI is becoming deeply embedded in firms, and where gaps are emerging.

**Findings are Based on Two Primary Data Sources:**

**01.** Real-world usage data from enterprise customers of OpenAI.

**02.** An OpenAI survey of 9,000 workers across almost 100 enterprises documenting patterns of AI adoption.

All analyses in this report are based on de-identified, aggregated enterprise usage data. Message content was classified using automated systems, and no OpenAI employee reviewed individual enterprise, business, or API customer data as part of this analysis.

---

### [中文翻译] 研究概述与方法论

**研究背景**：

过去三年，企业已经在广泛的用例和运营工作流中集成了 AI 系统。

这些部署提供了关于 AI 如何塑造工作的洞察——特别是在准确性标准高、工作流复杂、生产力或决策质量改进直接影响经济结果的环境中。因为世界上大部分经济价值活动发生在企业内部，企业采用模式提供了一个清晰的信号，表明 AI 今天在哪里提供价值，以及未来可能在哪里提供价值。

OpenAI 超过 100 万企业客户的规模和多样性提供了对这一转变的独特视角。本报告总结了来自 OpenAI 企业客户群的关键发现，以及这些模式对企业 AI 当前状态和轨迹的建议。通过检查采用如何在行业和职能之间变化，分析还突出了 AI 在企业中深度嵌入的地方，以及差距正在出现的地方。

**两大数据来源**：

1. **真实使用数据**：来自 OpenAI 企业客户的实际使用情况
2. **大规模调查**：对近 **100 家企业**的 **9,000 名员工**进行调查，记录 AI 采用模式

**隐私保护**：

- 所有分析基于**去识别化、聚合的企业使用数据**
- 消息内容使用自动化系统分类
- **没有 OpenAI 员工审查**个别企业、商业或 API 客户数据

**研究意义**：

这是迄今为止**最大规模的企业 AI 使用研究之一**，提供了 AI 如何塑造工作的洞察——特别是在准确性标准高、工作流复杂、生产力或决策质量改进直接影响经济结果的环境中。

---

### [ENGLISH ORIGINAL] Enterprise AI Usage is Accelerating and Deepening

Over the past year, enterprise AI adoption has increased substantially as organizations incorporate AI into repeatable, multi-step workflows across functions and business units. OpenAI now serves more than 7 million ChatGPT workplace seats, and ChatGPT Enterprise seats have increased approximately 9x year-over-year.

Since November 2024, weekly Enterprise messages have grown approximately 8x in aggregate, with the average worker sending 30% more messages. This growth reflects both more frequent use of ChatGPT and a deepening in the intensity of use.

Two shifts underscore the deepening integration of AI into core enterprise workflows.

**Custom GPTs and Projects are Enabling Deeper Workflow Integration:**

GPTs and Projects are configurable interfaces built on ChatGPT that can be tailored with instructions, knowledge, and custom actions, enabling workers to execute repeatable, multi-step tasks.

Weekly users of Custom GPTs and Projects have increased by approximately **19× year-to-date**. In recent months, approximately **20%** of all Enterprise messages were processed via a Custom GPT or Project. The most widely deployed GPTs either codify institutional knowledge into reusable assistants or automate workflows through integrations with internal systems. Some organizations have built a culture of developing and sharing Custom GPTs at scale. For example, BBVA regularly uses more than 4,000 GPTs, indicating that AI-driven workflows are increasingly implemented as persistent tools embedded in daily operations.

**Developer and API Workflows are Rapidly Scaling:**

Companies build on the API to integrate models directly into their products and systems with a high degree of control and customization. As firms transition from experimentation to production deployments, API consumption has rapidly increased. More than 9,000 organizations have now processed over 10 billion tokens, and nearly 200 have exceeded 1 trillion tokens.

Average reasoning token consumption per organization has increased by approximately **320x** in the past 12 months, suggesting that more intelligent models are being systematically integrated into expanding products and services. Codex, while still early in its enterprise lifecycle, is gaining rapid traction as teams adopt it for end-to-end software tasks: code generation, refactoring, testing, and debugging.

In the past six weeks, Codex engagement indicates growing penetration of AI-assisted development inside enterprises:
- **2x** Increase in weekly active users
- **50%** Approximate increase in weekly messages

---

### [中文翻译] 第一部分：企业 AI 使用正在加速和深化

#### 1. 使用量的爆炸性增长

**基础数据**：

- OpenAI 现在服务超过 **700 万 ChatGPT 工作席位**
- ChatGPT Enterprise 席位同比增长约 **9 倍**
- 自 2024 年 11 月以来，每周企业消息量总计增长约 **8 倍**
- 平均员工发送的消息增加 **30%**

**这种增长反映了两个趋势**：
1. ChatGPT 使用更频繁
2. 使用强度加深

**两个转变强调了 AI 深度集成到核心企业工作流中**。

#### 2. Custom GPTs 和 Projects：更深的工作流集成

**什么是 GPTs 和 Projects？**

GPTs 和 Projects 是构建在 ChatGPT 上的可配置界面，可以定制：
- 指令（Instructions）
- 知识库（Knowledge）
- 自定义操作（Custom Actions）

使员工能够执行**可重复的、多步骤的任务**。

**使用数据**：

- **Custom GPTs 和 Projects 每周用户**：年初至今增长约 **19 倍**
- **近几个月**：约 **20%** 的所有企业消息通过 Custom GPT 或 Project 处理

**最广泛部署的 GPTs 类型**：

1. **将机构知识编码为可重用助手**
2. **通过与内部系统集成自动化工作流**

**案例**：

BBVA 经常使用超过 **4,000 个 GPTs**，表明 AI 驱动的工作流正在越来越多地实现为嵌入日常操作的持久工具。

一些组织已经建立了大规模开发和共享 Custom GPTs 的文化。

#### 3. 开发者和 API 工作流快速扩展

**API 的作用**：

公司使用 API 直接将模型集成到产品和系统中，具有高度控制和定制能力。随着企业从实验转向生产部署，API 消耗迅速增加。

**使用规模**：

- **9,000+ 组织**已处理超过 **100 亿 token**
- **近 200 家公司**超过 **1 万亿 token**

**推理 token 增长**：

- 每个组织平均推理 token 消耗：过去 12 个月增长约 **320 倍**
- **这表明**：更智能的模型正在系统性地集成到扩展的产品和服务中

**Codex 的快速增长**：

Codex 虽然仍处于企业生命周期早期，但正在迅速获得关注，团队将其用于端到端软件任务：
- 代码生成（Code Generation）
- 重构（Refactoring）
- 测试（Testing）
- 调试（Debugging）

**过去 6 周 Codex 数据**：

- **每周活跃用户**：增长 **2 倍**
- **每周消息量**：增长约 **50%**

**解读**：AI 辅助开发在企业内部的渗透率正在增长。

---

### [ENGLISH ORIGINAL] Workers Report Measurable Value from Using AI

In most settings, AI enables workers to produce higher quality work faster. However, productivity alone does not fully reflect how AI is reshaping work. Survey data from almost 100 enterprises highlights key operational gains across functions, and shifts in who performs specialized and technical work.

**Enterprise Workers Report Time Saved and Improved Outcomes Across Functions:**

Seventy-five percent of surveyed workers report that using AI at work has improved either the speed or quality of their output. On average, ChatGPT Enterprise users attribute 40–60 minutes of time saved per active day to their use of AI, with data science, engineering, and communications workers saving more than average (60–80 minutes per day). Time saved per message varies by function: accounting and finance users report the largest benefits followed by analytics, communications, and engineering.

**These gains translate into broad operational improvements across functions:**

- 87% of IT workers report faster IT issue resolution
- 85% of marketing and product users report faster campaign execution
- 75% of HR professionals report improved employee engagement
- 73% of engineers report faster code delivery

These results indicate that productivity benefits are already materializing across core enterprise functions, not only in early-adopting technical roles.

**Technical Work Expands Beyond Traditional Role Boundaries:**

AI is not only accelerating existing work; it is also expanding the tasks and skills workers can perform. Several studies find that AI has an equalizing effect, disproportionately aiding lower performing workers. Consistent with these findings, 75% of workers report being able to complete tasks they previously could not perform, including programming support and code review, spreadsheet analysis and automation, technical tool development and troubleshooting, and custom GPT or agent design.

The broadening of individual capabilities is particularly apparent in technical settings, where non-technical teams are increasingly engaging in coding and data-analysis work that was previously confined to specialized roles. Among ChatGPT Enterprise users, coding-related messages have increased across all functions, and outside of engineering, IT, and research, coding-related messages have grown by an average of 36% over the past six months.

**Workers Report Greater Productivity from More Intensive AI Use:**

At the individual worker level, impact increases as workers deepen their use of AI. Across a large sample of workers, time saved is correlated with the use of more advanced ChatGPT features, including Deep Research, GPT-5 Thinking, and Image Generation. Workers consuming the most intelligence (as measured by credits used) report higher time savings. Workers who save more than 10 hours per week are not just using more intelligence, they are also using multiple models, engaging with more tools, and using AI across a wider range of tasks.

The >10 hours/week saved group uses **8x more credits** than the 0 hours/week saved group.

---

### [中文翻译] 第二部分：员工报告从使用 AI 中获得可测量的价值

在大多数情况下，AI 使员工能够更快地产生更高质量的工作。然而，生产力本身并不能完全反映 AI 如何重塑工作。来自近 100 家企业的调查数据突出了跨职能的关键运营收益，以及谁在执行专业化和技术工作方面的转变。

#### 1. 企业员工报告节省时间和改善跨职能成果

**总体满意度**：

**75%** 的受访员工报告，在工作中使用 AI 提高了输出的速度或质量。

**时间节省**：

| 用户类型 | 每日节省时间 | 特点 |
|---------|-------------|------|
| **普通用户** | 40-60 分钟 | ChatGPT Enterprise 用户平均 |
| **技术角色** | 60-80 分钟 | 数据科学、工程、通信员工节省更多 |

**按职能划分的时间节省效率**：

每条消息节省的时间因职能而异，以下职能报告最大收益：
1. **会计和金融**（Accounting & Finance）
2. **分析**（Analytics）
3. **通信**（Communications）
4. **工程**（Engineering）

**这些收益转化为跨职能的广泛运营改进**：

| 职能 | 改进指标 | 比例 |
|------|---------|------|
| **IT 员工** | 更快的 IT 问题解决 | **87%** |
| **营销和产品用户** | 更快的营销活动执行 | **85%** |
| **HR 专业人员** | 改善员工参与度 | **75%** |
| **工程师** | 更快的代码交付 | **73%** |

**关键结论**：

这些结果表明，生产力收益已经在核心企业职能中实现，**不仅仅局限于早期采用的技术角色**。

#### 2. 技术工作扩展到传统角色边界之外

**AI 不仅加速现有工作，还扩展了员工能够执行的任务和技能。**

**使能效应（Equalizing Effect）**：

多项研究发现，AI 具有均衡效应，**不成比例地帮助表现较低的员工**。

与这些发现一致：**75% 的员工**报告能够完成以前无法执行的任务，包括：
- 编程支持和代码审查
- 电子表格分析和自动化
- 技术工具开发和故障排除
- 自定义 GPT 或 agent 设计

**非技术团队的编程能力扩展**：

个人能力的拓宽在技术环境中特别明显：
- **非技术团队**越来越多地参与编程和数据分析工作（以前仅限于专业角色）
- 在 ChatGPT Enterprise 用户中，**所有职能**的编程相关消息都有所增加
- **工程、IT 和研究之外**，编程相关消息在过去 6 个月平均增长 **36%**

**关键洞察**：AI 正在改变"谁能做技术工作"的边界。

#### 3. 更密集使用 AI 的员工报告更高生产力

**个人层面的影响随 AI 使用深度增加而增加**：

在大量员工样本中，节省的时间与使用更高级 ChatGPT 功能相关，包括：
- **Deep Research**（深度研究）
- **GPT-5 Thinking**（GPT-5 思考）
- **Image Generation**（图像生成）

**"智能"消耗与时间节省的关系**：

消耗最多智能（通过使用的 credits 衡量）的员工报告更高的时间节省。

| 每周节省时间 | Credits 使用量（相对值） |
|------------|----------------------|
| 0-2 小时 | 低 |
| 2-5 小时 | 中 |
| 5-10 小时 | 中高 |
| **>10 小时** | **高（是 0 小时组的 8 倍）** |

**节省超过 10 小时/周的员工特征**：

不仅使用更多智能，他们还：
- 使用**多个模型**
- 使用**更多工具**
- 在**更广泛的任务类型**中使用 AI

**结论**：生产力收益随 AI 使用强度增加而增加。

---

### [ENGLISH ORIGINAL] Pace of Acceleration Varies Based on Industry and Geography

Over the last year we've seen overall rapid adoption as companies move from AI pilots to full deployments, and there are notable differences based on industry and geography.

**Growth is Rapid Across Most Industries:**

OpenAI customer growth is broad-based across industries, with the median sector expanding more than 6x year-over-year and even the slowest-growing sector exceeding 2x.

Technology, healthcare, and manufacturing show the fastest growth, while finance and professional services operate at the largest scale.

**Fastest-growing sectors (Year-over-year customer growth):**

| Sector | Growth |
|--------|--------|
| 01. Technology | 11x |
| 02. Healthcare | 8x |
| 03. Manufacturing | 7x |

In absolute terms, ChatGPT Enterprise customers are most concentrated today in professional services, finance, and technology, sectors that were early adopters and continue to lead in their scale of AI usage. Healthcare and manufacturing started from a much smaller base but are now among the fastest-growing sectors, rapidly closing the gap.

**API Use Cases by Industry:**

The API is most commonly used to build and scale customer-facing applications (e.g., in-product assistants, search, and automation), particularly by technology companies. But usage is diversifying: customer service and content generation now represent approximately 20% of API activity, and non-technology firm API use has grown 5x year-over-year. Taken together, this pattern suggests adoption is expanding beyond technology-led product embedding toward a broader set of operational and workflow deployments across industries.

**Enterprise Growth is Global and Accelerating:**

While early AI adoption was primarily U.S.-based, international growth is now accelerating rapidly:

**Australia, Brazil, the Netherlands, and France are growing faster than the global average:**

Percentage increase in number of paying business customers (Nov. '24 to Nov. '25):

| Country | Growth |
|---------|---------|
| Australia | 187% |
| Brazil | 161% |
| Netherlands | 153% |
| France | 146% |
| Canada | 144% |
| **Global average** | **143%** |
| United States | 142% |
| Germany | 138% |
| United Kingdom | 133% |
| Japan | 130% |

- ChatGPT usage among business customers continues to scale globally, with the United States, Germany, and Japan among the most active markets by message volume
- The United Kingdom and Germany now rank among the largest ChatGPT Enterprise markets outside the U.S. by number of customers
- International API customer growth has exceeded 70% over the last 6 months, with Japan having the largest number of corporate API customers outside of the U.S.

---

### [中文翻译] 第三部分：加速步伐因行业和地理位置而异

过去一年，我们看到随着公司从 AI 试点转向全面部署，整体采用迅速加速，并且基于行业和地理位置存在显著差异。

#### 1. 跨行业快速增长

OpenAI 客户增长在各行业中广泛分布，**中位数行业**同比扩张超过 **6 倍**，即使是增长最慢的行业也超过 **2 倍**。

**增长最快的三大行业**：

| 排名 | 行业 | 同比客户增长 |
|------|------|-------------|
| **01** | **Technology（技术）** | **11 倍** |
| **02** | **Healthcare（医疗）** | **8 倍** |
| **03** | **Manufacturing（制造业）** | **7 倍** |

技术、医疗和制造业显示最快增长，而金融和专业服务在规模上运营最大。

**规模最大的行业**（绝对规模）：

目前，ChatGPT Enterprise 客户最集中在：
1. **Professional Services（专业服务）**
2. **Finance（金融）**
3. **Technology（技术）**

这些行业是早期采用者，并继续在 AI 使用规模上领先。

**后来居上的行业**：

Healthcare 和 Manufacturing 从小得多的基础起步，但现在是增长最快的行业之一，正在迅速缩小差距。

#### 2. 不同行业的 API 使用模式

**API 最常用于构建和扩展面向客户的应用**（例如，产品内助手、搜索和自动化），特别是科技公司。但使用正在多样化：

- 客户服务和内容生成现在占 API 活动的约 **20%**
- 非技术公司 API 使用同比增长 **5 倍**

**这种模式表明**：采用正在从技术主导的产品嵌入扩展到跨行业的更广泛运营和工作流部署。

**按行业分析 API 使用重点**：

**技术公司（Technology）**：
- API 使用量同比增长 **5 倍**，用于扩展外部面向客户的应用
- 在编程工作流中领先，前沿模型（如 Codex）正在加速软件开发

**Top API 用例（Technology）**：
1. **应用内助手和搜索**（In-app Assistant & Search）
2. **Agentic 工作流自动化**（Agentic Workflow Automation）
3. **编程和开发者工具**（Coding & Developer Tools）
4. **客户支持**（Customer Support）
5. **数据分析、总结和提取**（Data Analysis, Summarization & Extraction）

**专业服务公司（Professional Services）**：

专业服务公司将 API 支出集中在编程和开发者工具上，构建定制工具：
- 加速交付
- 改善客户体验（通常通过个性化）
- 启用助手应用

**Top API 用例（Professional Services）**：
1. **编程和开发者工具**（Coding & Developer Tools）
2. **内容和创意生成**（Content & Creative Generation）
3. **应用内助手和搜索**（In-app Assistant & Search）
4. **客户支持**（Customer Support）
5. **Agentic 工作流自动化**（Agentic Workflow Automation）

**金融机构（Finance）**：

金融机构通常从客户支持开始，因为支持是大型、可扩展的成本中心，具有已证实的投资回报率。编程和开发者工具排名第二，因为公司投资于系统迁移和交易、风险和合规的定制应用。

**Top API 用例（Finance）**：
1. **客户支持**（Customer Support）
2. **编程和开发者工具**（Coding & Developer Tools）
3. **Agentic 工作流自动化**（Agentic Workflow Automation）
4. **应用内助手和搜索**（In-app Assistant & Search）
5. **数据分析、总结和提取**（Data Analysis, Summarization & Extraction）

#### 3. 企业增长是全球性的并且正在加速

虽然早期 AI 采用主要基于美国，但国际增长现在正在迅速加速：

**增长最快的市场**（业务客户增长）：

在最大市场中，以下国家显示最快增长，同比增长超过 **143%**：

| 排名 | 国家 | 同比增长 |
|------|------|---------|
| 01 | **Australia（澳大利亚）** | **187%** |
| 02 | **Brazil（巴西）** | **161%** |
| 03 | **Netherlands（荷兰）** | **153%** |
| 04 | **France（法国）** | **146%** |
| 05 | Canada（加拿大） | 144% |
| - | **全球平均** | **143%** |
| 06 | United States（美国） | 142% |
| 07 | Germany（德国） | 138% |
| 08 | United Kingdom（英国） | 133% |
| 09 | Japan（日本） | 130% |

**ChatGPT 使用量全球扩展**：

业务客户的 ChatGPT 使用继续在全球范围内扩展：
- **美国、德国和日本**按消息量计算是最活跃的市场

**ChatGPT Enterprise 市场**：

- **英国和德国**现在是美国以外最大的 ChatGPT Enterprise 市场（按客户数量）

**国际 API 增长**：

- 国际 API 客户增长在过去 6 个月超过 **70%**
- **日本**在美国以外拥有最多的企业 API 客户

---

### [ENGLISH ORIGINAL] The Growing Divide in AI Adoption

There are clear differences emerging in how AI is used across industries and among individuals within firms. Whether this gap widens or contracts will depend on how organizations approach change management and their ability to build the systems, skills, and operating models required to successfully deploy AI.

To understand this growing divide more deeply, it is useful to compare frontier workers (defined as those in the 95th percentile of adoption intensity) to the median worker. **Frontier workers generate 6x more messages than the median worker.** Even among those who work in data analytics, frontier workers use the data-analysis tool 16x more than the median.

The gaps are widest between frontier and median workers for writing, coding, and analysis. **Coding exhibits the largest relative gap in message volume, with frontier workers sending 17x as many messages as the median.**

**AI usage varies dramatically across tasks for frontier workers compared to median:**

Comparison of 95th percentile-to-median messages per user:
- Writing & communication: **11x**
- **Coding: 17x** (Largest relative gap)
- How-to & procedural guidance: **9x**
- Information gathering: **9x**
- Analysis & calculations: **10x**
- Creative media: **8x**

These differences matter. Usage data matched to survey results show that users who engage across roughly seven task types report five times more time saved than those who use only about four. In other words, **the benefits users realize from AI scale directly with depth of use.**

**Even among active ChatGPT Enterprise users, many have not tried some of the most capable tools.** Of monthly active users, 19% have never used data analysis, 14% have never used reasoning, and 12% have never used search. Among daily active users, those shares drop to 3%, 1%, and 1%, respectively.

**There's significant headroom for firms to increase their AI maturity.**

**At the firm level,** gaps in adoption intensity mirror those observed at the individual level. Frontier firms (95th percentile) generate approximately **2x more messages per seat** than the median enterprise and **7x more messages to GPTs**, indicating markedly deeper organizational integration and workflow standardization. These firms invest systematically in the infrastructure and operating models required to embed AI as core organizational capability rather than a peripheral productivity tool.

---

### [中文翻译] 第四部分：AI 采用中日益扩大的鸿沟

AI 在跨行业和公司内个人之间的使用方式出现了明显差异。这种差距是扩大还是缩小将取决于组织如何处理变革管理，以及他们构建成功部署 AI 所需的系统、技能和运营模式的能力。

#### 1. 个人层面：前沿员工 vs 中位数员工

为了更深入地理解这种日益扩大的鸿沟，将**前沿员工**（定义为采用强度第 95 百分位的员工）与中位数员工进行比较很有用。

**总体消息量差距**：

前沿员工生成的消息是中位数员工的 **6 倍**。

**数据分析差距**：

即使在从事数据分析工作的人员中，前沿员工使用数据分析工具的频率是中位数的 **16 倍**。

#### 2. 按任务类型分析：编程显示最大差距

**前沿员工 vs 中位数员工的差距因任务而异**：

| 任务类型 | 前沿/中位数比率 | 特点 |
|---------|---------------|------|
| **编程（Coding）** | **17 倍** | **最大相对差距** |
| **写作和通信** | 11 倍 | - |
| **分析和计算** | 10 倍 | - |
| **操作指南** | 9 倍 | - |
| **信息收集** | 9 倍 | - |
| **创意媒体** | 8 倍 | - |

**最关键的发现：编程是最大差距**

**编程显示消息量的最大相对差距，前沿员工发送的编程消息是中位数的 17 倍。**

**最重要的发现**：

> **编程是所有任务类型中"前沿用户"和"普通用户"差距最大的领域。**
> —— OpenAI State of Enterprise AI Report 2025

**这意味着什么**：

1. **编程比其他任务更依赖专业知识**：
   - 写文档、做演示的使用差距没这么大
   - 但编程的使用差距达到 **17 倍**
   - **原因**：编程需要深厚的领域知识来"验证"AI 输出

2. **专家优势在 AI 时代被放大**：
   - 前沿用户（通常是专家）能够：
     - 提出更复杂的问题
     - 更有效地使用 AI 工具
     - 更快地验证和迭代
   - 普通用户可能：
     - 不知道如何提问
     - 无法判断输出质量
     - 陷入"垃圾进，垃圾出"的循环

3. **AI 不是"民主化编程"的工具**：
   - 如果 AI 真的"降低了编程门槛"，使用差距应该缩小
   - 但数据显示：差距是 **17 倍**——这是**放大差距，而非缩小差距**

**这些差异很重要**：

使用数据与调查结果匹配显示，跨越约 **7 种任务类型**的用户报告的时间节省是仅使用约 **4 种任务类型**用户的 **5 倍**。换句话说，**用户从 AI 中实现的收益与使用深度直接相关。**

#### 3. 高级工具的使用差距

**即使是活跃用户，许多人也没有尝试最强大的工具**：

在 ChatGPT Enterprise 活跃用户中：

| 工具类型 | 月活跃用户从未使用比例 | 日活跃用户从未使用比例 |
|---------|---------------------|---------------------|
| **数据分析** | 19% | 3% |
| **推理功能** | 14% | 1% |
| **搜索功能** | 12% | 1% |

**重要发现**：
- 月活跃用户中，近 20% 从未使用数据分析工具
- 日活跃用户更可能探索和使用高级功能
- **这表明企业在 AI 成熟度方面有巨大的提升空间**

#### 4. 任务多样性与生产力收益的关系

**使用 AI 完成的不同任务类型越多，节省的时间越多**：

使用数据与调查结果显示，跨越约 **7 种任务类型**的用户报告的时间节省是仅使用约 **4 种任务类型**用户的 **5 倍**。

**任务类型示例**：
- 数据分析
- 编程
- 图像生成
- 翻译
- 写作
- 文本编辑
- 等等

**结论**：AI 的价值与使用深度直接相关。

#### 5. 企业层面的差距

**前沿企业 vs 中位数企业**：

| 指标 | 前沿/中位数比率 |
|------|---------------|
| **整体消息量（每座位）** | **2 倍** |
| **发送到 GPTs 的消息** | **7 倍** |

**前沿企业的特点**：
- 系统性投资于基础设施和运营模式
- 将 AI 嵌入为核心组织能力
- 而非作为外围生产力工具

**这表明组织层面的深度集成和工作流标准化至关重要。**

---

### [中文翻译] 非技术人员的编程使用增长

**编程民主化的另一面**：

- **非技术职能**的编程相关消息增长 **36%**

**这看似矛盾，但其实不矛盾**：

**学习工具 vs 生产力工具的区别**：

| 场景 | 用户类型 | 使用方式 | 结果 |
|------|---------|---------|------|
| **生产力工具** | 专家开发者 | 17 倍消息量，复杂任务 | 专家优势被放大 |
| **学习工具** | 非技术人员 | 简单脚本、数据分析 | 学习民主化 |

**解读**：
- **对专家**：AI 是"涡轮增压器"，让他们能做更复杂的项目
- **对新手**：AI 是"入门工具"，让他们能做简单的自动化

**但这不代表新手能做专家的工作**：
- 非技术人员的 36% 增长 vs 前沿用户的 17 倍使用量
- 这是两个不同的使用层级

---

### [ENGLISH ORIGINAL] AI Adoption and Business Impact: Case Evidence

The following case studies illustrate how AI is generating measurable business outcomes across a range of organizational contexts. Rather than a one-size-fits-all solution, their impact reflect the application of AI to specific operational and strategic challenges.

**External Research:**

A 2025 Boston Consulting Group (BCG) study found that over the past three years, AI leaders achieved **1.7x revenue growth, 3.6x greater total shareholder return, and 1.6x EBIT margin.** They also outperformed on non-financial measures such as patent output and employee satisfaction, linking AI maturity to both financial and organizational strength.

**Case Studies Include:**
- Intercom: Fin Voice (Realtime API reducing latency 48%, 53% call resolution rate)
- Lowe's: Mylow and Mylow Companion (nearly 1 million queries/month, conversion rate doubled)
- Indeed: AI-powered job matching (applications started +20%, hiring likelihood +38%)
- BBVA: Legal AI chatbot (9,000+ queries automated annually, 3 FTE redeployed)
- Oscar Health: Healthcare chatbots (58% instant answers, 39% no human escalation)
- Moderna: TPP development (weeks to hours, faster patient delivery)

---

### [中文翻译] AI 采用与商业影响：案例研究

OpenAI 报告包含了 6 个详细的企业案例研究，展示 AI 如何在不同行业和场景中产生可衡量的商业成果。这些案例涵盖了收入增长、客户体验改善、流程自动化和产品开发加速等多个方面。

**外部研究支持**：

2025 年波士顿咨询集团（BCG）研究发现，在过去三年中，AI 领先者实现了：
- **1.7 倍**的收入增长
- **3.6 倍**的股东总回报
- **1.6 倍**的 EBIT 利润率

同时在专利产出和员工满意度等非财务指标上也表现出色。

#### 案例 1：Intercom - Fin Voice（语音 AI 代理）

**Challenge:**
Intercom's AI agent Fin delivers industry-leading resolution rates across chat, email, and social channels, resolving millions of customer queries each month. But extending Fin to a new channel—the phone—introduced a new, critical challenge: latency.

**Solution:**
Intercom built Fin Voice on OpenAI's Realtime API to dramatically reduce latency and enable natural, interruption-friendly phone conversations.

**Impact:**
- **Latency decreased by 48%** since March through Fin Voice's use of the Realtime API
- **53% of calls end-to-end resolution rate** on average
- Calls requiring human agents are **resolved 40% faster** after Fin Voice completes initial steps
- **Saving customers hundreds of millions of dollars annually**

**挑战**：
Intercom 的 AI 代理 Fin 在聊天、电子邮件和社交渠道上提供行业领先的解决率，每月解决数百万客户查询。但将 Fin 扩展到新渠道——电话——带来了新的关键挑战：**延迟**。

**解决方案**：
Intercom 基于 OpenAI 的 **Realtime API** 构建了 Fin Voice，显著降低延迟并实现自然、可中断的电话对话。

**影响**：
- **延迟降低 48%**：自 3 月以来
- **53% 的呼叫端到端解决率**
- **人工处理速度提升 40%**
- **每年节省数亿美元**

#### 案例 2-6 概要

**Lowe's - Mylow**：每月近 100 万次查询，转化率翻倍以上，客户满意度提高 200 个基点

**Indeed - AI 职业匹配**：申请启动率提高 20%，录用可能性提高 38%，找工作速度提升 7 倍

**BBVA - 法律 AI 聊天机器人**：每年自动化超过 9,000 次查询，重新部署 3 个全职员工，实现法律服务部门年度节省 KPI 的 26%

**Oscar Health - 医疗保健聊天机器人**：58% 的福利问题即时回答，39% 的福利消息无需人工升级处理

**Moderna - TPP 开发**：核心分析步骤从几周缩短到几小时，加速患者服务

---

### [ENGLISH ORIGINAL] In Practice, Leading Firms Consistently Do Several Things

**Deep System Integration Through Enabling Context:**
They turn on connectors to give AI secure access to company data inside core tools, enabling context-aware responses and automated actions. Roughly one in four enterprises still has not taken this step.

**Workflow Standardization and Reuse:**
They actively promote the creation, sharing, and discovery of repeatable solutions for common tasks. GPTs often power this work, while the most sophisticated organizations embed API-powered assistants directly into core internal systems.

**Executive Leadership and Sponsorship:**
They set clear mandates, secure resources, and align teams, and create space for experimentation, all of which enable deployment at scale.

**Data Readiness and Evaluations:**
They codify institutional knowledge into machine-readable routines, build APIs for key data pipelines, and run continuous evaluations to track model performance on real-world outcomes.

**Deliberate Change Management:**
They build structures that speed organizational learning, combining centralized governance and training with distributed enablement through embedded AI champions.

**The AI landscape is evolving rapidly; OpenAI releases a new feature or capability roughly every three days. The primary constraints for organizations are no longer model performance or tooling, but rather organizational readiness.**

---

### [中文翻译] 领先企业的最佳实践

通过对前沿企业的分析，OpenAI 发现领先企业在实践中一致地做以下几件事：

#### 1. 深度系统集成（启用上下文）

**做法**：
- 打开连接器，让 AI 安全访问核心工具内的公司数据
- 支持上下文感知响应和自动化操作
- **但仍有约四分之一的企业尚未采取这一步骤**

**重要性**：提供组织上下文是 AI 发挥最大价值的基础

#### 2. 工作流标准化和重用

**做法**：
- 积极促进为常见任务创建、共享和发现可重复解决方案
- GPTs 通常为这项工作提供支持
- 最成熟的组织将 API 驱动的助手直接嵌入核心内部系统

**示例**：BBVA 定期使用 4,000 多个 GPTs

#### 3. 高管领导和支持

**做法**：
- 设定明确的授权
- 确保资源
- 协调团队
- 为实验创造空间

**重要性**：所有这些都能够实现大规模部署

#### 4. 数据准备和评估

**做法**：
- 将机构知识编码为机器可读的例程
- 为关键数据管道构建 API
- 运行持续评估以跟踪实际成果上的模型性能

**重要性**：确保 AI 输出的质量和可靠性

#### 5. 深思熟虑的变革管理

**做法**：
- 建立加速组织学习的结构
- 结合集中治理和培训
- 通过嵌入式 AI 倡导者进行分布式赋能

**关键洞察**：

> **AI 格局发展迅速；OpenAI 大约每三天发布一个新功能或能力。对组织而言，主要约束不再是模型性能或工具，而是组织准备度。**

---

### [ENGLISH ORIGINAL] Conclusion

Across OpenAI's more than 1 million business customers, AI is being embedded into an expanding range of workflows, products, and internal systems. Adoption is broad-based and accelerating across industries and regions, though depth of integration varies widely by organization.

The data suggest that **depth of use matters.** Workers and firms that make more consistent use of advanced tools, such as reasoning models, data analysis, Custom GPTs, Projects, and APIs, report larger productivity gains and broader task coverage than those whose use remains limited.

AI is also beginning to change **who performs certain types of technical work.** Coding and analytical tasks are increasingly showing up outside of traditional specialist roles, expanding what some non-technical teams are able to do. At the same time, industry patterns remain distinct, reflecting different operational needs across technology, professional services, finance, healthcare, manufacturing, and more.

Despite a growing divide in AI adoption, **enterprise AI is still in the early innings.** Firms have an opportunity to catch up by adopting the patterns of frontier workers and organizations. As enterprise AI matures, firms will increasingly translate AI capabilities into products and services that deliver new sources of value through faster iteration, deeper personalization, and new experiences. Organizations that succeed in bringing these capabilities into market-facing workflows will use AI not merely as a productivity tool, but as a durable engine of revenue growth and competitive advantage.

---

### [中文翻译] OpenAI 报告的结论

在 OpenAI 的 100 多万商业客户中，AI 正被嵌入到不断扩展的工作流程、产品和内部系统中。采用在各行业和地区广泛且加速，尽管集成深度因组织而异。

#### 使用深度至关重要

数据表明，**使用深度很重要**。与使用范围有限的员工和企业相比，更一致地使用高级工具（如推理模型、数据分析、Custom GPTs、Projects 和 APIs）的员工和企业报告的生产力提升更大，任务覆盖范围更广。

#### 技术工作的边界正在扩展

AI 也开始改变**谁执行某些类型的技术工作**。编程和分析任务越来越多地出现在传统专业角色之外，扩展了一些非技术团队能够做的事情。同时，行业模式保持独特，反映了技术、专业服务、金融、医疗、制造业等不同的运营需求。

#### 企业仍处于早期阶段

尽管 AI 采用出现了不断扩大的差距，但**企业 AI 仍处于早期阶段**。通过采用前沿员工和组织的模式，企业有机会迎头赶上。随着企业 AI 的成熟，企业将越来越多地将 AI 能力转化为产品和服务，通过更快的迭代、更深入的个性化和新体验提供新的价值来源。

**成功的关键**：
- 成功将这些能力引入面向市场的工作流程的组织
- 将使用 AI 不仅作为生产力工具
- 而且作为收入增长和竞争优势的持久引擎

**未来展望**：

下一阶段的企业 AI 将由以下因素塑造：
1. **更强的任务性能**：在经济上有价值的任务上表现更强
2. **更好的组织上下文理解**：更深入地理解组织特定的上下文
3. **从输出到委派的转变**：从要求模型提供输出转变为委派复杂的多步骤工作流程

随着这些能力的成熟，我们预计组织不仅会提高效率，还会发现服务客户和创造价值的新方式。

---

### OpenAI 报告的核心结论

#### 关键要点总结

**1. 企业 AI 仍处于"早期阶段"**：
- 尽管增长迅速（8 倍消息量，320 倍推理 token）
- 但大多数企业仍在探索最佳实践
- **结论**：还有巨大的增长空间

**2. 前沿用户的巨大优势**：
- 总体 6 倍消息量差距
- **编程 17 倍差距**
- **结论**：AI 工具的价值高度依赖使用者的技能水平

**3. 编程是"专家优势"最明显的领域**：
- 所有任务类型中差距最大
- **结论**：编程比其他知识工作更需要专业知识来有效使用 AI

**4. AI 既是放大器也是使能器**：
- 75% 的人能做以前做不了的任务（使能器）
- 技术人员节省 60-80 分钟/天（放大器）
- **结论**：AI 的价值是双重的

**5. 非技术人员也在使用编程功能**：
- 36% 增长
- **结论**：AI 确实降低了"简单编程"的门槛（学习工具效应）

---

### 对专家优势论的验证

OpenAI 的报告提供了最直接的数量证据：

| 专家优势论观点 | OpenAI 数据支持 |
|---------------|-----------------|
| **专家使用 AI 效率更高** | 前沿用户消息量是中位数的 **17 倍**（编程领域） |
| **专业知识是关键** | 编程是所有任务中差距**最大**的领域 |
| **AI 放大而非缩小差距** | 6 倍（总体）到 17 倍（编程）的使用差距 |
| **学习民主化仍然存在** | 非技术人员编程消息增长 36% |
| **但不代表替代专家** | 36% 增长 vs 17 倍使用量——不同层级 |

**最震撼的数据**：**17 倍**。

这个数字直接驳斥了"AI 让人人都能编程"的简单化叙事。

**如果 AI 真的"民主化"了编程**：
- ❌ 应该看到使用差距缩小
- ❌ 应该看到新手和专家使用量趋于一致

**但实际数据显示**：
- ✅ 专家使用 AI 的强度是普通人的 **17 倍**
- ✅ 这是所有任务类型中**最大的差距**
- ✅ **专家优势在 AI 时代被放大了**

---

## 三、两份报告的对比与综合分析

### 共同点：专家优势的双重验证

| 维度 | Anthropic（内部视角） | OpenAI（外部视角） | 共同结论 |
|------|---------------------|-------------------|---------|
| **专家变得更强** | 任务复杂度 +18.75% | 前沿用户 17 倍使用量 | AI 放大专家能力 |
| **AI 不能替代人** | 只能委托 0-20% 工作 | 75% 人能做新任务，但仍需人验证 | AI 是辅助工具 |
| **验证能力关键** | 55% 调试，42% 理解代码 | 编程差距最大（17 倍） | 理解和判断比生成更重要 |
| **新工作类型涌现** | 27% 原本不会做的任务 | 75% 能做以前做不了的事 | AI 拓展工作边界 |

### 差异点：内部 vs 外部视角

| 维度 | Anthropic（我们如何用 AI） | OpenAI（客户如何用 AI） |
|------|--------------------------|----------------------|
| **研究对象** | 132 名 Anthropic 员工（专家中的专家） | 9,000 名企业员工（各种水平） |
| **数据深度** | 20 万条对话 + 53 次深度访谈 | 100 万客户 + 9,000 人调查 |
| **核心关注** | 技能发展、职业焦虑、工作方式变化 | 生产力、使用差距、行业趋势 |
| **发现重点** | "我们担心技能退化" | "前沿用户远超普通用户" |
| **研究诚实度** | 坦承 5 大局限性 | 相对乐观的企业报告 |

### 最重要的交叉验证

**Anthropic 内部观察**：
> "工程师和研究员最常使用 Claude 修复代码错误和学习代码库——调试和代码理解是最常见的用途。"

**OpenAI 外部数据**：
> "编程是所有任务类型中'前沿用户'和'普通用户'差距最大的领域——前沿用户发送的编程消息是中位数的 17 倍。"

**综合结论**：
- **为什么编程差距最大？** 因为编程最需要"调试和理解代码"的能力
- **为什么调试和理解最重要？** 因为这需要深厚的专业知识
- **为什么只有专家能高效使用 AI？** 因为 AI 生成代码很容易，但验证代码需要专业知识

**这形成了一个完美的逻辑闭环**：

```
专业知识 → 能验证 AI 输出 → 能高效使用 AI（17 倍） → 解决更复杂问题 → 专业知识进一步增强
   ↑                                                                            ↓
   ←←←←←←←←←←←←←←←←←← 正反馈循环 ←←←←←←←←←←←←←←←←←←←
```

**新手则陷入相反循环**：

```
缺乏专业知识 → 无法验证 AI 输出 → 接受错误代码 → 积累技术债 → 技能停滞
   ↑                                                               ↓
   ←←←←←←←←←←←←←← 负反馈循环 ←←←←←←←←←←←←←←←
```

---

## 四、对"专家优势论"的终极验证

### 核心论点的数据支撑

**论点 1：只有专家才能最大化利用 LLM 加速工作**

**证据**：
- ✅ OpenAI：前沿用户编程消息是中位数的 **17 倍**
- ✅ Anthropic：任务复杂度从 3.2 增长到 3.8
- ✅ Anthropic：员工变得"更全栈"——跨领域工作能力增强

**论点 2：AI 不是替代专业知识，而是放大专业知识**

**证据**：
- ✅ Anthropic：只能"完全委托" 0-20% 的工作
- ✅ Anthropic：55% 用于调试，42% 用于代码理解——都需要深厚专业知识
- ✅ OpenAI：75% 的人能做新任务，但**仍需人类验证和判断**

**论点 3：验证能力是核心竞争力**

**证据**：
- ✅ Anthropic：最常用途不是"生成代码"，而是"理解和修复代码"
- ✅ OpenAI：编程是差距最大的领域——因为验证最难
- ✅ Anthropic：员工担心"监督 AI 输出的能力"退化

**论点 4：学习民主化与专家优势并存**

**证据**：
- ✅ OpenAI：非技术人员编程消息增长 36%（学习工具效应）
- ✅ OpenAI：但前沿用户仍然是 17 倍（专家优势仍在）
- ✅ Anthropic：27% 新增任务类型（AI 拓展边界）

---

## 五、实践启示：我们应该如何使用 AI？

### 给专家的建议（基于两份报告）

**1. 拥抱 AI，但警惕技能退化**

Anthropic 的员工——世界上最懂 AI 的人——都在担心技能退化。

**行动建议**：
- 定期"无 AI 练习"：每周花时间手写代码，保持基础技能
- 刻意练习验证能力：不要"Accept All"，而是批判性审查每一行 AI 代码
- 关注"为什么"而非"是什么"：理解架构决策背后的原因

**2. 用 AI 拓展边界，而非替代核心工作**

**Anthropic 的发现**：
> "我对任务越兴奋，就越不太可能使用 Claude。"

**实践建议**：
- ✅ 用 AI 做"不想做但必须做"的任务（边界工作）
- ✅ 用 AI 做"不熟悉但容易验证"的任务（跨领域工作）
- ❌ 不用 AI 做"你最擅长且最想做"的任务（核心竞争力）

**3. 把调试和理解作为主要用途**

**数据支持**：
- Anthropic：55% 调试，42% 理解
- OpenAI：编程差距 17 倍

**实践建议**：
- 用 AI 快速理解陌生代码库
- 用 AI 帮助调试复杂 bug
- 用 AI 解释第三方库的工作原理
- **但最终判断仍然靠你自己**

### 给新手的建议（基于数据现实）

**1. 承认现实：AI 不能让你一夜成为专家**

**OpenAI 数据**：
- 前沿用户 17 倍使用量
- 这不是工具问题，是技能问题

**实践建议**：
- 不要期待 AI 替代学习
- 把 AI 当"苏格拉底导师"，而非"答案机器"
- 先打基础，再用 AI 加速

**2. 用 AI 作为学习工具，而非生产力工具**

**区别**：

| 学习工具用法 | 生产力工具用法 |
|-------------|--------------|
| "为什么这段代码这样写？" | "帮我写这段代码" |
| "这个错误是什么原因？" | "帮我修这个 bug" |
| "解释一下这个算法" | "给我一个排序算法" |

**Anthropic 的警告**：
- 简单复制 AI 代码 → 技能停滞
- 用 AI 辅助理解 → 加速学习

**3. 接受非线性成长路径**

**OpenAI 数据**：
- 75% 的人能做以前做不了的任务
- 非技术人员编程消息增长 36%

**这意味着**：
- ✅ 你可以用 AI 快速尝试新领域（Top-Down Learning）
- ✅ 你可以用 AI 完成简单自动化任务
- ❌ 但不要误以为这就是"成为开发者"

---

## 六、未来趋势预测

### 基于两份报告的推断

**1. 专家优势会继续扩大**

**Anthropic 数据**：
- 任务复杂度 6 个月内从 3.2 增长到 3.8
- 连续自主动作从 10 增长到 20

**OpenAI 数据**：
- 推理 token 消耗增长 320 倍
- 前沿用户 17 倍使用量

**推断**：
- AI 工具越强大，对使用者的要求越高
- 未来的"17 倍"可能变成"50 倍"
- **马太效应**：强者愈强

**2. "验证工程师"将成为新职业**

**Anthropic 发现**：
- 最常见用途：调试（55%）和理解代码（42%）
- 员工担心"监督 AI 输出的能力"退化

**推断**：
- 未来的高级工程师不是"写代码最快的人"
- 而是"判断代码质量最准的人"
- 关键技能：架构审查、安全审计、性能优化、代码理解

**3. 企业将面临"技能空洞化"风险**

**Anthropic 的警告**：
- 员工长期焦虑："AI 最终会做所有事情"
- 导师机会减少
- 知识传递受阻

**推断**：
- 企业可能出现"高级专家 + AI"的极端化团队结构
- 中级工程师的成长路径被打断
- 未来可能缺乏"从初级成长为高级"的人才管道

**解决方案**：
- 企业需要刻意保留"无 AI 时间"用于技能培养
- 重新设计导师制度
- 平衡短期效率和长期人才培养

**4. AI 公司自己也在探索答案**

**Anthropic 的诚实**：
> "我们将自己定位为'负责任的工作场所转型实验室'，计划在 2026 年发布更具体的策略。"

**这意味着**：
- 即使是 OpenAI 和 Anthropic，也没有"标准答案"
- 所有人都在摸索
- **这是一个共同的实验，而非已解决的问题**

---

## 七、总结：数据告诉我们的真相

### 最重要的三个数字

**1. 17 倍（OpenAI）**
- 前沿用户编程消息是中位数的 17 倍
- **意义**：AI 时代，专家优势不是缩小了，而是被放大了

**2. 0-20%（Anthropic）**
- 员工只能"完全委托"给 AI 的工作量
- **意义**：AI 远未达到"替代"人类的程度

**3. 55% + 42% = 97%（Anthropic）**
- 调试（55%）+ 代码理解（42%）占据最主要用途
- **意义**：AI 的价值不是"生成代码"，而是"帮助理解和修复代码"

### 对"专家优势论"的终极回答

**问题**：AI 会让编程变得人人可做吗？

**两份报告的答案**：

**Anthropic 的回答**：
- 不，我们的工程师（世界顶级专家）仍然只能委托 0-20% 的工作给 AI
- 他们最常用 AI 做的是调试和理解代码——这需要深厚专业知识

**OpenAI 的回答**：
- 不，前沿用户的编程消息是普通用户的 **17 倍**
- 编程是所有任务中专家优势最明显的领域

### 最后的思考

两份报告共同指向一个真相：

**AI 不是"民主化"编程的工具，而是"放大"专业知识的工具。**

- 对专家：AI 是涡轮增压器，让你能解决更复杂的问题
- 对新手：AI 是学习工具，但需要正确的使用方法

**专家优势不仅没有消失，反而被放大了。**

但这不是精英论——这是客观数据的呈现。

**学习民主化仍然存在**：
- 非技术人员编程消息增长 36%（OpenAI）
- 27% 新增任务类型（Anthropic）

**但学习民主化 ≠ 专业知识被取代**：
- 前沿用户 17 倍使用量（OpenAI）
- 任务复杂度持续增长（Anthropic）

**未来属于**：
- 掌握基础知识，然后用 AI 加速的人
- 理解"为什么"，而非只知道"是什么"的人
- 能够验证 AI 输出，而非盲目接受的人

**未来不属于**：
- 试图用 AI 跳过学习的人
- 把 AI 当"答案机器"而非"学习工具"的人
- 相信"AI 会替代一切"的人

---

## 参考资料

### Anthropic 官方研究
- [How AI Is Transforming Work at Anthropic - Official Research Report (Aug 2025)](https://www.anthropic.com/research/how-ai-is-transforming-work-at-anthropic)
- [Anthropic 英文原文完整版](./Anthropic_How_AI_is_Transforming_Work_at_Anthropic_English_Original.md)

### OpenAI 官方报告
- [The State of Enterprise AI 2025 - OpenAI Official Report](https://openai.com/index/the-state-of-enterprise-ai-2025-report/)
- [The State of Enterprise AI 2025 - OpenAI PDF Report](https://cdn.openai.com/pdf/7ef17d82-96bf-4dd1-9df2-228f7f377a29/the-state-of-enterprise-ai_2025-report.pdf)
- [OpenAI 英文原文完整版](./OpenAI_The_State_of_Enterprise_AI_2025_English_Original.md)

### 第三方分析
- [OpenAI State of Enterprise AI Report 2025: Complete Analysis - ALM Corp](https://almcorp.com/blog/openai-state-of-enterprise-ai-report-2025/)
- [OpenAI Report Reveals 6x Productivity Gap - VentureBeat](https://venturebeat.com/ai/openai-report-reveals-a-6x-productivity-gap-between-ai-power-users-and)
- [What OpenAI's Data Reveals About the Future of Work - Inkeep](https://inkeep.com/blog/openai-enterprise-ai-adoption)
- [2025 The State of Generative AI in the Enterprise - Menlo Ventures](https://menlovc.com/perspective/2025-the-state-of-generative-ai-in-the-enterprise/)

---

**文档结束**

*本文档采用中英双语对照格式，基于 2025 年 Anthropic 和 OpenAI 发布的官方报告及相关研究，力求准确传达原文数据和观点。如有疑问，请参考原始报告。*

*完整英文原文已单独保存，可通过上方参考资料链接查看。*
