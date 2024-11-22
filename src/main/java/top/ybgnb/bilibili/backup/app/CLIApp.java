package top.ybgnb.bilibili.backup.app;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.business.BusinessRunner;
import top.ybgnb.bilibili.backup.app.business.BusinessType;
import top.ybgnb.bilibili.backup.app.menu.AppMainMenu;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;

import java.util.Scanner;


/**
 * @ClassName CliApp
 * @Description 命令行程序
 * @Author hzhilong
 * @Time 2024/9/28
 * @Version 1.0
 */
@Slf4j
public class CLIApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // 显示主菜单
        AppMainMenu.chooseBusiness(sc);
    }

}
