#!/bin/bash
#autor:young1lin
# 需要安装 go 的环境
SRC_DIR='pwd'
DST_DIR='pwd'/../src/main/

echo source:           ${SRC_DIR}
echo detination root:  ${DST_DIR}

function ensure_implementations(){
  gem list | grep ruby-protocol-buffers || sudo gem install ruby-protocol-buffers
  go get -u github.com/golang/protobuf/{proto,protoc-gen-go}
}

function gen(){
	D=$1
	echo $D
	OUT=$DST_DIR/$D
	mkdir -p $OUT
	protoc -I=$SRC_DIR --${D}_out=$OUT $SRC_DIR/customer.proto
}

ensure_implementations

gen java
gen python
