1.使用ps命令查看使用cpu最高的进程id
ps aux|head -1;ps aux|grep -v PID|sort -rn -k +4|head
2.使用ps命令查看是哪个目录下的
ps -ef | grep 3713
3.以上步骤确定了是tomcat的问题，使用top命令查看该进程下占用cpu资源最高的线程
top -Hp 3713
ctrl c 退出









