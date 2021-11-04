Git Log 常见使用

```shell
git log 
git log --oneline
git log --graph
git log --all
git log -n4 # 表示显示最近4个
# 以上三个可以组合使用
git help --web log
```



# ./git/HEAD 

是存的当前分支信息

# .git/config 

是存的本地仓库的信息

```properties
[core]
	repositoryformatversion = 0
	filemode = true
	bare = false
	logallrefupdates = true
	ignorecase = true
	precomposeunicode = true
```

# .git/.refs/heads/master

存的是分支，指向的什么东西

# .git/.refs/heads/tags

里程碑

# git cat-file -t [hash value]

查看 Git 系统的文件的类型

- commit
- blob
- tree

t 是 type 的意思

# git cat-file -p 

是看内容

# .git/objects

info	

pack （如果提交的文件太多，就会 pack 到一起）

![image-20210621182425437](/Users/linyoung/Library/Application Support/typora-user-images/image-20210621182425437.png)

# gitk -all

 来弹出 Git UI 界面

# commit 切记要和分支一起关联

# git diff

```shell
git diff HEAD HEAD~1
git diff HEAD HEAD~2
# 比较两个分支不同的差异
git diff temp master
# 比较两个分支两个具体的文件
git diff temp master -- index.html
# 指定两个 commit 的不同
git diff [hash1] [hash2] -- index.html
```

查看暂存区和 HEAD 的比较

```shell
git diff --cached
```

工作区和暂存区的差异

```shell
git diff
```

只对其中几个文件ganxingqu

```shell
git diff -- README.md
```

# 清除分支

```shell
git branch -av
git branch -d [分支名字]
git branch -D [分支名字] # 强制删除
```

# git commit --amend

修正

# git rebase -i [git 提交的 hash]

不连续的 commit 合并

找到提交的，上次提交的 hash，就行了

```shell
git rebase --continue
```

里面的 pick 是挑捡，s 是 squash 来 use commit 但是不用 commit 信息

# 暂存区的内容都不要了，恢复成 HEAD

```shell
git reset HEAD
# 恢复指定的文件名
git reset HEAD -- [文件名]
# 有些 commit 不想要了，直接重制到指定的 commit
git reset --hard [提交的 hash 值]
```

## 工作区恢复到暂存区的内容

想变暂存区的，用 reset

想变工作区的，用 checkout

```shell
git checkout -- [文件名]
```

# git rm [文件名]



# git stash

把当前的内容，存到当前一个区域

```shell
# 把之前存的东西拿出来，并且之前的内容还有内容，并且 stash 里面还有
git stash apply
# 把当前的东西拿出来，并且弹出来
git stash pop
```

# .gitignore

加了 / 表示文文件夹，不加表示文件



