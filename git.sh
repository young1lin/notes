#!/bin/bash
#autor:young1lin
#url:https://young1lin.me
if  [ ! -n "$1" ] ;then
    echo "请输入要提交的模块名称"
    exit 1
fi
message=""

for i in "$*"; do
    message+=$i
done

git add .
echo "-----------------------------"
echo "add all ...."
echo "-----------------------------"

git commit -m "$message update"
echo "-----------------------------"
echo "commit info"
echo "-----------------------------"
git push
echo "-----------------------------"
echo "git push ok!"
echo "-----------------------------"
