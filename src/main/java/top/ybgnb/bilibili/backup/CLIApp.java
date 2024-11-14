package top.ybgnb.bilibili.backup;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.selector.BusinessSelector;

import java.util.Scanner;

import static top.ybgnb.bilibili.backup.utils.CommonUtil.*;

/**
 * @ClassName CliApp
 * @Description
 * @Author hzhilong
 * @Time 2024/9/28
 * @Version 1.0
 */
@Slf4j
public class CLIApp {

    public static void main(String[] args) {
        menuForBusinessChoice();
    }

    private static void menuForBusinessChoice() {
        Scanner sc = new Scanner(System.in);
        scannerThreadLocal.set(sc);
        do {
            log.info("请选择对应功能的序号:");
            BuType[] buTypes = BuType.values();
            for (int i = 0; i < buTypes.length; i++) {
                log.info("{}: {}", i, buTypes[i].getCnName());
            }

            String inputedData = sc.nextLine();
            if (inputDataIsInvalid(buTypes, inputedData)) continue;
            BuType buType = buTypes[Integer.parseInt(inputedData)];
            if (BuType.EXIT.equals(buType)) return;

            buTypeThreadLocal.set(buType);
            BusinessSelector.processBusiness(buType, null);

        } while (true);
    }

    private static boolean inputDataIsInvalid(BuType[] buTypes, String inputedData) {
        try {
            int integer = Integer.parseInt(inputedData);
            if (integer > -1 && integer <= buTypes.length) {
                return false;
            } else {
                log.info("输入错误，请重新选择");
                return true;
            }
        } catch (Exception e) {
            log.info("输入错误，请重新选择");
            return true;
        }
    }

}
