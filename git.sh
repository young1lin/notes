#!/bin/bash
#autor young1lin
#url https://young1lin.me
if  [ ! -n "$1" ] ;then
    echo "请输入要提交的模块名称"
    exit 1
fi
message=""

for i in "$*"; do
   message=$message$i
done
commit_info="${message} update"

git add ${message}

echo "add ${message} ...."

git commit -m "$commit_info"

echo "commit info is $commit_info"

git pull

echo "git pull"

git push

echo "git push ok!"
