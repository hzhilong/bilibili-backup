package io.github.hzhilong.bilibili.backup.app.cli;

import io.github.hzhilong.bilibili.backup.app.cli.btn.callback.YesOrNo;
import io.github.hzhilong.bilibili.backup.app.cli.btn.impl.InputYes;

import java.util.Scanner;

/**
 * 菜单基类
 *
 * @author hzhilong
 * @version 1.0
 */
public class BaseMenu {

    public static void inputYes(Scanner scanner, YesOrNo yesOrNo) {
        new InputYes(scanner).showBtn(yesOrNo);
    }

}
