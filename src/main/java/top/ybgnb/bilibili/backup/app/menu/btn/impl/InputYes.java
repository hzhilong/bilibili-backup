package top.ybgnb.bilibili.backup.app.menu.btn.impl;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.menu.btn.BaseBtn;
import top.ybgnb.bilibili.backup.app.menu.btn.callback.YesOrNo;

import java.util.Scanner;

/**
 * @ClassName InputYes
 * @Description 输入y以确定
 * @Author hzhilong
 * @Time 2024/11/25
 * @Version 1.0
 */
@Slf4j
public class InputYes extends BaseBtn<YesOrNo> {

    private Scanner scanner;

    private YesOrNo callback;

    public InputYes(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void showBtn(YesOrNo callback) {
        log.info("输入y：是\t输入其他：否");
        String nextLine = scanner.nextLine();
        if ("Y".equals(nextLine) || "y".equals(nextLine)) {
            callback.yes();
        } else {
            callback.no();
        }
    }
}
