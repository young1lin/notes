from langchain_text_splitters import RecursiveCharacterTextSplitter

# 示例代码
GAME_CODE = """
class CombatSystem:
   def __init__(self):
       self.health = 100
       self.stamina = 100
       self.state = "IDLE"
       self.attack_patterns = {
           "NORMAL": 10,
           "SPECIAL": 30,
           "ULTIMATE": 50
       }
   def update(self, delta_time):
       self._update_stats(delta_time)
       self._handle_combat()
   def _update_stats(self, delta_time):
       self.stamina = min(100, self.stamina + 5 * delta_time)
   def _handle_combat(self):
       if self.state == "ATTACKING":
           self._execute_attack()
   def _execute_attack(self):
       if self.stamina >= self.attack_patterns["SPECIAL"]:
           damage = 50
           self.stamina -= self.attack_patterns["SPECIAL"]
           return damage
       return self.attack_patterns["NORMAL"]
class InventorySystem:
   def __init__(self):
       self.items = {}
       self.capacity = 20
       self.gold = 0
   def add_item(self, item_id, quantity):
       if len(self.items) < self.capacity:
           if item_id in self.items:
               self.items[item_id] += quantity
           else:
               self.items[item_id] = quantity
   def remove_item(self, item_id, quantity):
       if item_id in self.items:
           self.items[item_id] -= quantity
           if self.items[item_id] <= 0:
               del self.items[item_id]
   def get_item_count(self, item_id):
       return self.items.get(item_id, 0)
class QuestSystem:
   def __init__(self):
       self.active_quests = {}
       self.completed_quests = set()
       self.quest_log = []
   def add_quest(self, quest_id, quest_data):
       if quest_id not in self.active_quests:
           self.active_quests[quest_id] = quest_data
           self.quest_log.append(f"Started quest: {quest_data['name']}")
   def complete_quest(self, quest_id):
       if quest_id in self.active_quests:
           self.completed_quests.add(quest_id)
           del self.active_quests[quest_id]
   def get_active_quests(self):
       return list(self.active_quests.keys())
"""

# 创建文本分割器
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=1000,  # 每个块的大小
    chunk_overlap=00,  # 相邻块之间的重叠大小
    # separators=["\n\n", "\n", " ", ""]  # 分割符列表
)

# 执行分块
text_chunks = text_splitter.create_documents([GAME_CODE])

# 打印分块结果
print("\n=== 代码分块结果 ===")
for i, chunk in enumerate(text_chunks, 1):
    print(f"\n--- 第 {i} 个代码块 ---")
    print(f"内容:\n{chunk.page_content}")
    print(f"元数据: {chunk.metadata}")
    print("-" * 50)
