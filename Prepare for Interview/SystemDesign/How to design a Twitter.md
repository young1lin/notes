**Scenario 1: Post Tweets**

From https://www.youtube.com/watch?v=PMCdWr6ejpw

# Overview

1. Clarify the requirements
2. Capacity Estimation
3. System APIs
4. High-level System Design
5. Data Storage
6. Scalability

# Clarify requirements

Clarify requirements and goals of the system

- Requirements
- Traffic size(e.g., Daily Active User)

Nobody expcet you to design a complete system in 30-45 mins

Discuss the functionalities, align with interviewers on components to focus.

## Functional Requirement

1. **Tweet**
   1. **Create**
   2. **Delete**
2. **Timeline/Feed**
   1. **Home** 
   2. **User**
3. Follow a user
4. Like a tweet
5. Search tweets
6. ...

## Non-Functional Requirement

CAP Theory, BASE Theory

- Consistency 
  - Every read receives the most recent write or an error 
  - Scarifice: Eventual consistenchy
- Availability
  - Every request receives a (non-error) response, without the guarantee that it contains the most recent wirte
  - **Scalable**
    - **Performance: low Latency**
- Partition tolerance(Fault Tolerance)
  - The system continues to operate despite an arbitrary number of messages being dropped(or delayed) by the net work between nodes

# Capacity Estimation

Assumption:

- 200 million DAU, 100 million new tweets
- Each user: visit home timeline 5 times; other user timeline 3 times
- Each timeline/page has 20 tweets
- Each tweet has size 280(140 characters) bytes, metadata 30 bytes 
  - Per photo: 200KB, 20% tweets have images
  - Per video: 2MB, 10% tweets have video, 30% videos will be watched

## Storage Estimate

- Write size daily:
  - Text:
    - 100M new tweets * (280 + 30) Bytes/tweet = 31GB/day
  - Image
    - 100M new tweets * 20% has image * 200 KB per image = 4TB/day
  - Video
    - 100M new tweets * 10% has video * 2MB per video = 20TB/day
- Total
  - 31 GB + 4TB + 6TB = 24TB/day

# Bandwidth Estimate

Daily Read Tweets Volume: 

- 200M * (5 home visit + 3 user visit) * 20 tweets/page = 32B tweets/day

Daliy Read Bandwidth

- Text: 32GB * 280 bytes / 86400 = 100MB/s
- Image: 32B * 20% tweets has image * 200 KB per image / 86400 = 14GB/s
- Video: 32B * 10% tweets has video * 30% got waathced * 2MB per video / 86400 = 20GB/s
- Total: 35GB/s

 # System APIs

- postTweet(userToken, string tweet)
- deleteTweet(userToken, string tweetId)
- likeOrUnlikeTweet(userToken, string tweetId, bool like)
- readHomeTimeLine(userToken, int pageSize, opt string pageToken)
- readUserTimeLine(userToken, int pageSize, opt string pageToken)

# High-level System Design

**Scenario 1: Post Tweets**

![Tweet System Design -1 Scenario: Post tweets.png](https://i.loli.net/2021/02/04/KFfeIm8cWUkw4qx.png)

**Scenario 2: Visit User Timeline**

- Need very low latency（～200ms）
  - Too  time conusming for querying tweets and joined together the fly

![Twitter System Design - 2 - Visit User Timeline.png](https://i.loli.net/2021/02/04/jaP1pJudFLNrqKA.png)

**Scenario3: Visit Home Timeline**

![Tweet Design - 3 - Visit Home Timeline.png](https://i.loli.net/2021/02/04/QawrFs2SvzTLdMo.png)

# Home Timeline（cont'd）

Naive solution: Pull mode

- How 
  - Fetch tweets from N followers from DB, merge and return
- Pros
  - Write is fast: O(1)
- Cons
  - Read is slow: O (N) DB reads

**Better Solution: Push mode**

- How 
  - Maintain a feed list in cache for each uesr
  - Fanout on write
- Pros
  - Read is fast: O(1) from the feed list in cache
- Cons
  - Write need more efforts: O(N) write for each new tweet
    - Aysnc tasks
  - Delay in showing latest tweets(eventual consistency)

 **Fan out on write**

- Not efficient for users with huge amount of followers（~> 10k）

Hybrid Solution

- Non-hot users:
  - fan out on write (push): write to user timeline cache 
  - do not fanout on non-active users
- Hot users:
  - fan int on read (pull): read during timeline request from tweets cache, and aggregate with results from non-hot users

# Data Storage

![Data Storage.png](https://i.loli.net/2021/02/04/gr7nbvujl8fEQVL.png)

-  SQL database
  - user table
- NoSQL database
  - timelines
- File System
  - Media file: image, audio, video

# Scalability

- Identify potential bottlenecks
- Discussion solutions, focusing on tradeoffs
  - Data sharding
    - Data store, cache
  - Load balancing
    - E.g., user<->application server; application server<->cache server;application server<->db
  - Data caching
    - Read heavy

## Sharding

分库分表

Horizontal scaling

Option 1: Shard by tweet's creation time 

Pros:

	- Limited shards to query

Cons:

- Hot/Cold data issue
- New shard fill up quickly

Option2: Shard by hash(userId): store all the data of a user on a single shard

Pros:

- Simple
- Query user timeline is straightforward

Cons:

- Home timeline still needs to query multiple shards
- Non-uniform distribution of storage
  - User data might not be able to fit into a single shard
- Hot users
- Availability

Option 3: shard by hash(tweetId)

Pros:

- Uniform distribution
- High availability

Cons:

- Need to query all shards in order to generrate user/home timeline

# Caching

