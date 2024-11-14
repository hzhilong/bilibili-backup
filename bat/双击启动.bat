@echo off
color 3F
title=哔哩哔哩自动化工具

if "%1" equ "" (
start /max call %0 1
exit
)

set bin=%cd%\bin

"%bin%\jre\bin\java" -jar "%bin%\bilibili-backup.jar"

echo 按任意键退出
pause>nul
exit
