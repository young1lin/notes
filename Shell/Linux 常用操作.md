# vi

esc + u 

撤回上一步操作

# top

查看系统进程使用资源情况，例如虚拟内存，cpu 使用率、内存、进程 id 等。

# history

之前的命令加上管道符 | grep 过滤指定输入的命令

# sudo 

sudo -i 提升至 root 用户权限 

# chmod 

chmod +x 赋予文件执行权限

# scp

```shell
scp [local_file] [target_file_path]
# for example 
scp log.log root@192.168.1.2:/home/berry
```

# cp

```shell
cp [file_path] [target_file_name_path]
cp log.log log.txt
```

# tail

```shell
tail -f catalina.out;
# 一直变化的文件末尾 20 行
tail -f 20 catalina.out ;
# 从 20 行显示到文件末尾
tail +20 file;
```

# ls

``` shell
# 使用长格式列出文件及目录信息
ls -l;
# 显示所有文件及目录 (包括以“.”开头的隐藏文件)
ls -a;
# 将文件以相反次序显示(默认依英文字母次序)
ls -r;
# 根据最后的修改时间排序
ls -t;
# 同 -a ，但不列出 “.” (当前目录) 及 “..” (父目录)
ls -A;
# 根据文件大小排序
ls -s;
# 递归列出所有子目录
ls -r;
ls -als;

```



# 更改 host 文件 

```shell
vi /etc/hosts
```

