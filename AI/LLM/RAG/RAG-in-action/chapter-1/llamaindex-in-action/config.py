import os

# Get the absolute path to your data file
current_dir = os.path.dirname(os.path.abspath(__file__))
BLACK_WUKONG_DATA_FILE_PATH = os.path.join(current_dir, "..", "90-文档-Data", "黑悟空", "设定.txt") 

if __name__ == "__main__":
    print(BLACK_WUKONG_DATA_FILE_PATH)
