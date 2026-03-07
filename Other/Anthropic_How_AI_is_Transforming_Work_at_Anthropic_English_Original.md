# How AI Is Transforming Work at Anthropic

**Original English Article**
**Published**: August 2025
**Source**: https://www.anthropic.com/research/how-ai-is-transforming-work-at-anthropic

---

## Introduction

How is AI changing the way we work? Anthropic's previous research on AI's economic impacts examined the labor market broadly across various job categories. However, this study turns inward to examine early AI adopters within the organization itself.

In August 2025, Anthropic surveyed 132 engineers and researchers, conducted 53 in-depth qualitative interviews, and analyzed internal Claude Code usage data spanning 200,000 transcripts from February and August 2025. The research reveals that AI is radically transforming software development work, generating both opportunities and concerns.

---

## Key Research Findings

### Survey Data Results

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

### Interview Insights

**AI Delegation Strategies:**

Engineers develop systematic approaches for determining what to delegate:

- Tasks outside their expertise but low complexity
- Work that is easily verifiable before deployment
- Well-defined or self-contained components
- Code where quality standards are less critical
- Repetitive or tedious assignments
- Tasks requiring less than 10 minutes of implementation time

One engineer noted: "The more excited I am to do the task, the more likely I am to not use Claude."

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

## Claude Code Usage Trends

### Increasing Autonomy

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

### Papercut Fixes

8.6% of Claude Code tasks involve minor quality-of-life improvements previously deprioritized:
- Code refactoring for maintainability
- Creating performance visualization tools
- Building terminal shortcuts

These small fixes cumulatively improve workflow efficiency and reduce friction.

### Team-Specific Patterns

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

## Research Methodology

### Survey Design

Anthropic distributed surveys through internal Slack channels and direct team outreach. Response collection employed both convenience sampling (68 responses) and purposive sampling across 20 diverse teams (64 responses from 207 direct outreach contacts, yielding 31% response rate).

The organization shared complete survey questions publicly to allow external evaluation and adaptation.

### Interview Process

53 survey respondents participated in qualitative interviews selected on a first-response basis, covering diverse roles across research and product functions.

### Data Analysis

Claude Code analysis employed privacy-preserving methods, examining 200,000 internal transcripts from February and August 2025. Task complexity was rated on a 1-5 scale. Tool calls and human turns were counted to measure autonomy progression.

---

## Limitations and Caveats

### Selection Bias

Respondents likely skewed toward those particularly engaged with Claude or holding strong opinions. Those with neutral experiences may be underrepresented.

### Response Bias

Responses may reflect social desirability bias, as participation was non-anonymous and all respondents were Anthropic employees. Recency bias affected recall of productivity and usage patterns from 12 months prior.

### Productivity Measurement Challenges

Self-reported productivity metrics are difficult to validate precisely. External research from METR suggested experienced developers on familiar codebases overestimate AI productivity gains. However, Anthropic employees may have developed superior "strategic AI delegation skills" not captured in comparable studies.

The research notes ambiguity regarding where time savings are reinvested—whether toward additional engineering tasks, non-engineering work, Claude interaction and review, or activities outside work. Task categorization frameworks don't capture all possible time allocation patterns.

### Claude Code Analysis Limitations

Proportionate sampling across time periods allows measurement of relative task distribution changes only, not absolute changes in total work volume. When reporting feature implementation increased from 14% to 37%, this indicates relative proportion changes, not necessarily increased absolute feature work.

### Model Capability Context

Data collection occurred when Claude Sonnet 4 and Claude Opus 4 represented state-of-the-art capabilities. Subsequent model advances may have shifted observed patterns.

---

## Looking Forward

### Internal Initiatives

Anthropic is pursuing several exploratory directions:

- Examining how teams can collaborate more effectively
- Supporting professional development in an AI-augmented context
- Establishing best practices for AI-integrated work guided by their AI fluency framework
- Expanding research beyond engineers to understand transformation across organizational roles
- Supporting external organizations like CodePath as they adapt computer science curricula

### Structural Considerations

The organization is considering:

- New pathways for role evolution and reskilling
- Approaches to maintain technical expertise amid AI delegation
- Methods to preserve meaningful colleague collaboration
- Learning and mentorship models suited to AI-augmented work

Anthropic frames itself as "a laboratory for responsible workplace transition," aiming to study and experiment with thoughtful navigation of AI's impact.

---

## Key Takeaway

More capable AI delivers clear productivity benefits while raising critical questions about technical expertise maintenance, meaningful human collaboration, and preparation for uncertain futures requiring new approaches to learning, mentorship, and career development. The organization expects to share concrete plans in 2026 as thinking matures.

---

**End of Article**
