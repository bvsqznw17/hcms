echo start upload by pscp

@REM cd D:\cbw\workplace\vs-workspace\chjd\chjd-admin\target
@REM rename chjd-admin.war chjd.war
@REM echo Flower@123|pscp -P 13342 hcms.jar flower@121.199.30.10:/usr/local/hcms/dockerfiles/hcmsapi
@REM echo Flower@123|pscp -P 13342 builddocker.sh flower@121.199.30.10:/usr/local/hcms/dockerfiles/hcmsapi
@REM echo Flower@123|pscp -P 13342 Dockerfile flower@121.199.30.10:/usr/local/hcms/dockerfiles/hcmsapi
@REM echo Flower@123|pscp -P 13342 docker-compose.yml flower@121.199.30.10:/usr/local/hcms/dockerfiles
@REM echo Flower@123|pscp -P 13342 build_all.sh flower@121.199.30.10:/usr/local/hcms/dockerfiles
@REM echo Flower@123|pscp -P 13342 nginx.conf flower@121.199.30.10:/etc/nginx/nginx.conf
echo Flower@123|pscp -P 13342 version.json flower@121.199.30.10:/home/hcms/appStoreFile/version.json
echo Flower@123|pscp -P 13342 hcms.apk flower@121.199.30.10:/home/hcms/appStoreFile/hcms.apk
@REM echo Flower@123|pscp -P 13342 pfx-password.txt flower@121.199.30.10:/usr/local/tomcat9/cert
@REM echo Flower@123|pscp -P 13342 aftersale.charmhigh.com.pfx flower@121.199.30.10:/usr/local/tomcat9/cert

pause