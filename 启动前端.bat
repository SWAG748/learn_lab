@echo off
chcp 65001 >nul
cd /d "%~dp0web"
if not exist "node_modules" call npm install
echo 正在启动前端 http://localhost:5173
call npm run dev
pause
