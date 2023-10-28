#!/bin/bash
#docker部分

echo '================打包完成，开始制作镜像================'

echo '================停止容器 hcmsapi================'
docker stop hcmsapi
echo '================删除容器 hcmsapi================'
docker rm hcmsapi
echo '================删除镜像 hcmsapi:last================'
docker rmi hcmsapi:last
echo '================修改上一次的镜像名 hcmsapi:latest================'
docker tag hcmsapi:latest hcmsapi:last
echo '================build 镜像 hcmsapi:latest================'
docker build -t hcmsapi:latest  .
# echo '================运行容器 chjdapi================'
# docker run --name=chjdapi --restart always -d -p 18090:8880 \
# -v /home/tmpFile/:/home/tmpFile \
# -v /home/chjd/uploadPath:/home/chjd/uploadPath \
# -v /logs:/logs \
# -v /usr/local/dockerfiles/chjdapi/webapps:/usr/local/tomcat/webapps \
# -v /home/chjd/tomcat/logs:/usr/local/tomcat/logs \
# -v /home/chjd/tomcat/conf:/usr/local/tomcat/conf \
# -v /home/chjd/tomcat/cert:/usr/local/tomcat/cert \
# chjdapi:latest

echo "finished!"
# echo '================部署完成================'
