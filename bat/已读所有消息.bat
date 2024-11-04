@echo off
color 3F
title=哔哩哔哩一键已读所有消息

if "%1" equ "" (
start /max call %0 1
exit
)

echo 是否一键已读所有消息？
echo 按任意键继续
pause >nul 2>&1

set bin=%cd%\bin

"%bin%\jre\bin\java" -jar "%bin%\bilibili-backup.jar" read_all_msg

echo 按任意键关闭窗口
pause>nul
exit
