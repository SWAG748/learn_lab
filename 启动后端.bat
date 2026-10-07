@echo off
chcp 65001 >nul
cd /d "%~dp0server"
if exist "D:\develop\jdk\bin\java.exe" set "JAVA_HOME=D:\develop\jdk"
if exist "D:\develop\apache-maven-3.9.16\bin\mvn.cmd" set "PATH=D:\develop\apache-maven-3.9.16\bin;%PATH%"
echo 正在启动后端 http://localhost:8080
echo 第一次会下载 Maven 依赖，需要几分钟。
set "MVN_SETTINGS="
if exist "D:\develop\maven-settings.xml" set "MVN_SETTINGS=-s D:\develop\maven-settings.xml"
call mvn %MVN_SETTINGS% spring-boot:run
pause
