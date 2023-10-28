@echo off
echo.
echo [信息] 执行Web项目的打包操作（使用Maven）
echo.

setlocal enabledelayedexpansion

rem 获取当前脚本所在目录
set "scriptPath=%~dp0"

rem 执行Maven命令进行打包
pushd "%scriptPath%.."
call mvn clean package -Dmaven.test.skip=true
popd

echo.
echo [信息] 复制并重命名jar文件
echo.

rem 复制并重命名jar文件
set "sourceFile=D:\cbw\workplace\vs-workspace\hcms\ruoyi-admin\target\ruoyi-admin-3.8.3.jar"
set "destPath=D:\cbw\workplace\vs-workspace\hcms\docker"
set "destFile=hcms.jar"

copy /Y "%sourceFile%" "%destPath%\%destFile%"

echo.
echo [信息] 上传到服务器
echo.

rem 上传jar文件到远程服务器
set "remoteUser=flower"
set "remoteHost=121.199.30.10"
set "remotePort=13342"
set "remotePath=/usr/local/hcms/dockerfiles/hcmsapi"

echo Flower@123| pscp -P %remotePort% "%destPath%\%destFile%" %remoteUser%@%remoteHost%:%remotePath%

echo.
pause