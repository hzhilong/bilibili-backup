@echo off
color 3F
title=哔哩哔哩账号还原

if "%1" equ "" (
start /max call %0 1
exit
)

echo 是否还原？
echo 按任意键继续
pause >nul 2>&1

set bin=%cd%\bin

"%bin%\jre\bin\java" -jar "%bin%\bilibili-backup.jar" restore

echo 按任意键关闭窗口
pause>nul
exit
