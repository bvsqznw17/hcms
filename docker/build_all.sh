#!/bin/bash
# 调用build其他shell的脚本

source ./chjdapi/builddocker.sh
source ./chjdvue/builddocker.sh
# source ./diskapi/builddocker.sh
# source ./diskvue/builddocker.sh

#docker部分

docker-compose up
#docker-compose up -d