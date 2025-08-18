import fitz  # PyMuPDF库，用于处理PDF文件
import matplotlib.patches as patches  # 用于在图像上绘制多边形
import matplotlib.pyplot as plt  # Matplotlib库，用于绘图
from PIL import Image  # 用于图像处理

def render_pdf_page(file_path, doc_list, page_number):
    """
    渲染指定PDF页面并绘制段落分类框。

    参数：
    - file_path: str，PDF文件路径。
    - doc_list: list，包含段落信息的文档列表，每个元素是一个字典，包含段落元数据。
    - page_number: int，要渲染的页面号（从1开始计数）。
    """
    # 打开PDF文件并加载指定页面
    pdf_page = fitz.open(file_path).load_page(page_number - 1)
    segments = [doc.metadata for doc in doc_list if doc.metadata.get("page_number") == page_number]

    # 将PDF页面渲染为位图图像
    pix = pdf_page.get_pixmap()
    pil_image = Image.frombytes("RGB", [pix.width, pix.height], pix.samples)

    # 创建绘图环境
    fig, ax = plt.subplots(figsize=(10, 10))
    ax.imshow(pil_image)

    # 定义类别颜色映射
    category_to_color = {"Title": "orchid", "Image": "forestgreen", "Table": "tomato"}
    categories = set()

    # 绘制段落标注框
    for segment in segments:
        points = segment["coordinates"]["points"]
        layout_width = segment["coordinates"]["layout_width"]
        layout_height = segment["coordinates"]["layout_height"]
        scaled_points = [
            (x * pix.width / layout_width, y * pix.height / layout_height) for x, y in points
        ]
        box_color = category_to_color.get(segment["category"], "deepskyblue")
        categories.add(segment["category"])
        rect = patches.Polygon(scaled_points, linewidth=1, edgecolor=box_color, facecolor="none")
        ax.add_patch(rect)

    # 添加图例
    legend_handles = [patches.Patch(color="deepskyblue", label="Text")]
    for category, color in category_to_color.items():
        if category in categories:
            legend_handles.append(patches.Patch(color=color, label=category))
    ax.axis("off")
    ax.legend(handles=legend_handles, loc="upper right")
    plt.tight_layout()

