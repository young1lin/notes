#!/bin/bash
#autor:young1lin
#url:https://young1lin.me
if  [ ! -n "$1" ] ;then
    echo "请输入要提交的模块名称"
    exit 1
fi
message=""

for i in "$*"; do
   message=$message$i
done
commit_info="${message} update"

git add .
echo "-----------------------------"
echo "add all ...."
echo "-----------------------------"

git commit -m "$commit_info"
echo "-----------------------------"
echo "commit info is $commit_info"
echo "-----------------------------"
git push
echo "-----------------------------"
echo "git push ok!"
echo "-----------------------------"
