package io.github.hzhilong.bilibili.backup;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.app.cli.AppMainMenu;

import java.util.Scanner;


/**
 * 命令行程序
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class CLIApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // 显示主菜单
        AppMainMenu.chooseBusiness(sc);
    }

}
