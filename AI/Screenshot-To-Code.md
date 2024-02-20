从零开始，使用 Screenshot To Code.

# 前言

这里访问的所有网站，在中国大陆是几乎无法访问的，所以需要你有个能隐藏你 IP 地址的东西。还有注册 OpenAI 网站邮箱可以直接用 Google 账号登录，或者注册一个邮箱。

# 手机号验证

1. 进入网页 https://openai.com/ 点击右上角登录
2. 进入网页 https://platform.openai.com/docs/overview
3. 找到左侧边栏 API keys
![](https://s2.loli.net/2023/11/27/yq7CwiZKk5I3uHA.png)
4. 点击验证手机号
5. 进入这个网站 https://sms-activate.org/
![](https://s2.loli.net/2023/11/27/YI2u4hnelGAa7tw.png)
6. 点击右上角【充值】
7. 往下拉选择支付宝，不要选择支付宝 HK
8. 充值 2 美元就够了
9. 左侧边栏搜索 openai
10. 建议选择美国的
	![image.png](https://s2.loli.net/2023/11/27/dkgYqNi4c2yoeIv.png)
11. 购买完毕后，20 分钟内可以收到短信。注意，复制的时候，需要把第一位的 1 给去除掉，再复制到 OpenAI 验证号码的页面。
12. 复制好手机号后，刷新 sms 页面，复制验证码到 OpenAI 页面即可。
13. 现在就已经验证好手机号，生成一个 API key。如下图所示
![image.png](https://s2.loli.net/2023/11/27/qG6dZ9xPCi5c8Na.png)


# 申请虚拟信用卡

1. 进入正规的官网 https://www.nobepay.com 
2. 登录绑定你的邮箱，邀请码随便搜一个就行了。
3. 第一件事就是先充值，最少充值 500 人民币，用支付宝就行了

![image.png](https://s2.loli.net/2023/11/28/3dxPjs5gDrfVAze.png)

4. 充值完成后，点击左侧【我的卡片】-> 【快速开卡】
5. 选择好你的扣款账户，充值金额会算出来，建议只冲 10 美金试试水，等汇率低下来再充。
6. 选择好能开卡的卡段
![image.png](https://s2.loli.net/2023/11/28/P5eKt6rBZTwFNCf.png)
7. 随便填一填，开卡。
8. 左侧卡片列表，找到你的卡片，点【管理】->【查看卡片】
9. 一些地址，CVV 码什么的，都可以复制。

# OpenAI 添加支付

就把上一步开卡的信息，填一填到这里就行了，充值个 5 美元就行，加上赠送的 5 美元，够了。

![image.png](https://s2.loli.net/2023/11/28/jXaSoEqh1InVTUc.png)

# ScreenshotToCode

## 下载项目

## 后端

### 安装 Python

1. 进入这里 https://www.python.org/downloads/windows/    当然，你要是 MacOS 的可以去下 MacOS 的版本。
2. 下载 3.10.x 版本的，我装的就是避免版本不一致导致无畏的错误，Python Minor version change 也是会删代码的，不会给已废弃的标识。
3. 一步步确认安装就行了。
4. 一定要把其他版本的 Python 删干净了！！会有一堆的问题。并且环境变量检查一下是不是其他版本的环境变量已经没有了。
### 安装必要依赖

1. 创建一个文件夹来 clone 项目
2. 执行 `git clone git@github.com:abi/screenshot-to-code.git`
3. 进入 backend 文件夹
4. 在 backend 文件夹下，新建文件 requirements.txt 文件，文件内容如下
```txt
certifi==2023.7.22
exceptiongroup==1.1.3
h11==0.14.0
idna==3.4
sniffio==1.3.0
anyio==3.7.1
colorama==0.4.6
httpcore==1.0.2
typing-extensions==4.8.0
click==8.1.7
distro==1.8.0
httpx==0.25.1
pydantic==1.10.13
soupsieve==2.5
starlette==0.27.0
tqdm==4.66.1
beautifulsoup4==4.12.2
fastapi==0.95.2
openai==1.2.4
python-dotenv==1.0.0
uvicorn==0.24.0.post1
websockets==12.0
```

执行下面语句

```shell 
pip install -r requirements.txt
```
### 启动后端

```shell
uvicorn main:app --reload --port 7001
```

日志输出像这样的，就可以了。

![image.png](https://s2.loli.net/2023/11/28/1thGf6iLsmcSVTK.png)
## 前端

### 安装 NodeJs

必须要大于 v16 版本，不然后续会报错，如果安装过大于 v16 版本，跳过此步骤。

下载 https://nodejs.cn/download/

一步步点下一步就行了
### 安装 Yarn（不是大数据那个）

```shell
npm install -g yarn
yarn --version
```

### 安装依赖

```shell
yarn
```
### 启动前端

```shell
yarn dev
```
## 使用

1. 打开页面 http://localhost:5173
2. 使用截图软件（推荐 Snipaste）随便截取一张网页的图片。
3. 粘贴到页面上
这时候会报错，没有 OpenAI key，点击设置，这里填上你的密钥。（我看了下代码，controller 层的逻辑都在 main.py 中，刚好页面上有个设置看了，优先以你传的密钥为准）

![image.png](https://s2.loli.net/2023/11/28/6a9WPONtRBlJVQy.png)

### 样例 1
#### 原图

保存后会自动扫描你的截图，然后开始扫描出代码
扫描这样一张图片，花了 0.08 美元。
![image.png](https://s2.loli.net/2023/11/28/PFaToCgMbOK6iz7.png)
#### 结果

![image.png](https://s2.loli.net/2023/11/28/aW3uCOmQHvgbzwS.png)
#### 前端代码

![image.png](https://s2.loli.net/2023/11/28/2iDeLwTdu5x7WK3.png)

#### 花费

![image.png](https://s2.loli.net/2023/11/28/Bw26CEKSLvY5HUN.png)


### 样例 2
#### 原图
![image.png](https://s2.loli.net/2023/11/28/jRfS9rIPhak5bBJ.png)

#### 结果

![image.png](https://s2.loli.net/2023/11/28/ajoGumvUMzAXPYS.png)

#### 前端代码

![image.png](https://s2.loli.net/2023/11/28/2pvkgIUWq5jYb3y.png)

#### 花费

同样是 0.08 美元，0.16 减去上面的 0.08

![image.png](https://s2.loli.net/2023/11/28/hqPK2X7BbdUD3TF.png)

### 结论

生成的代码，怎么说呢，一般般，大家都是用前端 UI 框架写的，组件化开发。生成的代码都是原始的 HTML 标签，还有待完善。主要是 prompts.py 文件，提示词，得改这个。最近还兴起 Prompts 工程师，其实这个门槛很低，不建议做。
# 引用

1. [还不会注册和使用ChatGPT Plus的同学，赶快看过了，否则你可能会被淘汰哦！](https://www.bilibili.com/read/cv25932153/)
2. [How To Install and Use the Yarn Package Manager for Node.js](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-the-yarn-package-manager-for-node-js)