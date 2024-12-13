package io.github.hzhilong.bilibili.backup.app.cli.btn.impl;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.app.cli.btn.BaseBtn;
import io.github.hzhilong.bilibili.backup.app.cli.btn.callback.YesOrNo;

import java.util.Scanner;

/**
 * 输入y以确定
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class InputYes extends BaseBtn<YesOrNo> {

    private Scanner scanner;

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
