package top.ybgnb.bilibili.backup.app.menu;

import top.ybgnb.bilibili.backup.app.menu.btn.callback.YesOrNo;
import top.ybgnb.bilibili.backup.app.menu.btn.impl.InputYes;

import java.util.Scanner;

/**
 * @ClassName BaseMenu
 * @Description 菜单基类
 * @Author hzhilong
 * @Time 2024/11/25
 * @Version 1.0
 */
public class BaseMenu {

    public static void inputYes(Scanner scanner, YesOrNo yesOrNo) {
        new InputYes(scanner).showBtn(yesOrNo);
    }

}
