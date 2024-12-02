package top.ybgnb.bilibili.backup;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.menu.AppMainMenu;

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
