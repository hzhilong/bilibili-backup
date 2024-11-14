package top.ybgnb.bilibili.backup;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import top.ybgnb.bilibili.backup.app.BilibiliBackup;
import top.ybgnb.bilibili.backup.app.BilibiliReadAllMsg;
import top.ybgnb.bilibili.backup.app.BilibiliRestore;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.selector.BusinessSelector;
import top.ybgnb.bilibili.backup.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.userInfoCallback.UserInfoCallback;
import top.ybgnb.bilibili.backup.userInfoCallback.DefaultUserInfoCallback;
import top.ybgnb.bilibili.backup.user.User;
import top.ybgnb.bilibili.backup.utils.CookieUtil;
import top.ybgnb.bilibili.backup.utils.ItemChoiceUtil;
import top.ybgnb.bilibili.backup.utils.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
        manuForBusinessChoice();
    }

    private static void manuForBusinessChoice() {
        Scanner sc = new Scanner(System.in);
        scannerThreadLocal.set(sc);
        do {
            log.info("请选择对应功能的序号:");
            BuType[] buTypes = BuType.values();
            for (int i = 0; i < buTypes.length; i++) {
                log.info(i + ": " + buTypes[i].getCnName());
            }
            String inputedData = sc.nextLine();
            if (inputDataIsInvalid(buTypes.length, inputedData)) {
                continue;
            }
            BuType buType = buTypes[Integer.valueOf(inputedData)];
            buTypeThreadLocal.set(buType);
            
            BusinessSelector.processBusiness(buType,null);
            
        } while(true);
    }

    private static boolean inputDataIsInvalid(int buTypeLength, String inputedData) {
        try {
            Integer integer = Integer.valueOf(inputedData);
            if (integer > -1 && integer <= buTypeLength) {
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
