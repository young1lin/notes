# AGENTS.md

This file provides guidance to coding agents working in this repository.

`CLAUDE.md` is the primary repository rule file.

`AGENTS.md` is a consolidated view of `CLAUDE.md` plus imported rules under `.claude/rules` style workflows. If there is a conflict, follow `CLAUDE.md` first.

## Repository Overview

This is a personal knowledge base managed with Obsidian. The repository contains technical notes, learning materials, and code examples across multiple software engineering domains. Content is bilingual and organized by topic.

## Repository Structure

- `AI/` - AI and LLM notes, RAG examples, PyTorch material, AI tooling
- `Architecture/` - architecture patterns and system design
- `Blog/` - technical blog drafts and articles
- `Books/` - reading notes and summaries
- `Cloud Native/` - Docker, Kubernetes, cloud-native docs and sample projects
- `Data-Processing/` - Kafka, Flink, HBase, MySQL, Redis, MyBatis
- `Datastructure&Algorithm/` - algorithms, data structures, LeetCode material
- `Domain Driven Design/` - DDD notes and implementations
- `Elasticsearch/` - Elasticsearch notes
- `English/` - English learning material
- `Go/`, `Java8/`, `JVM/`, `Java调优/` - language and runtime notes
- `Microservices/` - microservice architecture notes and demos
- `Network/`, `OS/` - systems and networking material
- `PrepareForInterview/` - interview prep and system design
- `Python/` - Python notes
- `Spring/` - Spring framework material
- `TestAutomation/` - testing and automation
- `Web3/` - blockchain and crypto notes

## Obsidian Context

This repository is used with Obsidian and includes plugin-driven workflows.

- `.obsidian/` contains workspace and plugin configuration.
- The repository may be auto-synced by `obsidian-git`.
- Notes may depend on plugins such as `dataview`, `tasks`, `excalidraw`, `calendar`, and AsciiDoc support.

## Working Rules

### Search Rules

- Use English keywords first when searching code, file paths, APIs, framework names, and technical terms.
- Search Chinese only when the target is clearly Chinese prose or a specific Chinese term.
- Prefer `rg` and `rg --files` for discovery.

### Content Rules

- Respect the original language of the file when editing. Do not rewrite Chinese notes into English or the reverse unless asked.
- Preserve existing file naming patterns. Chinese file names are normal in this repository.
- Most value in this repository is in Markdown documentation. Optimize for factual accuracy, structure, and readability.
- When editing documentation, keep surrounding conventions intact instead of imposing a new template.

### Editing Rules

- Many subdirectories are standalone demos with their own build systems. Scope changes narrowly.
- Code examples under topic folders may be runnable projects. Treat each project as self-contained.
- Do not make repo-wide mechanical edits unless explicitly asked.

### Git Rules

- Be careful with manual git operations because `obsidian-git` may also write commits.
- If asked to commit, keep the scope narrow and avoid bundling unrelated note changes.
- The repository includes `git.sh`, but use it only when it matches the requested workflow.

### Build and Test Rules

- If a change touches a standalone Java, Python, or other sample project, run checks only in the affected project.
- Do not assume a single root build exists.
- If verification is not practical, say so explicitly and state what was not run.

### Research and Source Rules

- Prefer updating an existing note over creating duplicate notes on the same topic.
- When adding sourced technical content, include enough citation detail in the note for later verification.
- If a note depends on recent product behavior, APIs, limits, pricing, or release status, verify it against current primary sources before writing.

## Evidence-Based Content Rule

### Core Principle

If it is a fact, prove it.

If you cannot prove it, stop calling it a fact.

Opinion, analysis, and prediction are allowed. Do not present them as facts.

### Evidence Tiers

- `T1`: primary material from the vendor, platform, or tool owner. Official docs, changelogs, engineering posts.
- `T2`: recognized expert practitioner material. Books, talks, courses, technical blogs with real standing.
- `T3`: community consensus. Requires at least two independent sources.
- `T4`: tutorial interpretation, author judgment, teaching framing, prediction.

### Hard Rules

1. Every factual claim needs a source trail.
   Statements about product behavior, feature existence, pricing, limits, benchmarks, statistics, chronology, or technical capability must be traceable to a source.
2. Strong claims need strong proof.
   Terms such as `best`, `fastest`, `only`, `always`, `never`, and similar absolutes require `T1` or `T2`. If that proof does not exist, downgrade the statement to `T4` and narrow the wording.
3. Community repetition is not enough.
   Claims based on forum chatter or repeated blog claims count as `T3` and need at least two independent sources.
4. Predictions are never facts.
   Mark forecasts and forward-looking claims as `T4` and explain the reasoning.
5. Surface disagreement.
   When credible sources disagree, show the conflict first, then state the judgment and why.
6. Label interpretation.
   Use explicit markers such as `**[Tutorial perspective]**` or `**[Author's analysis]**` for non-factual content.

### What Requires Evidence

- feature existence or product behavior: `T1`
- benchmarks and performance claims: `T1` or `T2`
- expert recommendations or quoted guidance: `T2`
- statistics and quantitative claims: `T1`, `T2`, or `T3`
- workflow advice and teaching framing: `T4` is acceptable if clearly marked
- predictions: always `T4`

### Final Check

Before finalizing a factual note:

- every factual claim has a source or gets removed
- every opinion is marked or clearly framed as `T4`
- every strong claim has evidence strong enough to support it

## Voice and Tone Rule

### Core Principle

Write like an analyst. Not like an assistant trying to sound pleasant.

No padding. No flattery. No fake warmth. Start with substance.

### Writing Rules

1. Cut ruthlessly.
   Remove sentences that only transition, soften, or add polish without adding meaning.
2. Ban filler.
   Avoid stock phrases such as `it is worth noting`, `in terms of`, `all in all`, or similar empty scaffolding.
3. Open with the point.
   Start with the verdict, mechanism, tradeoff, or failure mode.
4. Use hard sentences.
   Prefer short, direct sentences. Use longer ones only when they carry technical weight.
5. Make clear judgments.
   If one option is better, say so and explain why.
6. Use concrete words.
   Prefer specific terms such as `fragile`, `wasteful`, `bloated`, or `misleading` over vague hedging.
7. Break bad assumptions fast.
   If an assumption is weak, challenge it directly and move to the reasoning.
8. No emotional pampering.
   Do not cheerlead or perform empathy in technical writing.
9. No fake neutrality.
   Do not present obviously unequal options as if they are equally sound.

### Boundary Condition

Cold style does not excuse sloppy claims.

- Keep factual content evidence-backed.
- Mark interpretation, prediction, and judgment as `T4` when required by the evidence rule.
