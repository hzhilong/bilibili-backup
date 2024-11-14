package top.ybgnb.bilibili.backup.service.impl;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.BilibiliBackup;
import top.ybgnb.bilibili.backup.app.BilibiliRestore;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.service.BaseBusinessService;
import top.ybgnb.bilibili.backup.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.user.User;
import top.ybgnb.bilibili.backup.userInfoCallback.DefaultUserInfoCallback;
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
 * @author Dream
 */
@Slf4j
public class RestoreBusinessService implements BaseBusinessService {
    @Override
    public Object process(Object requestMsg) throws BusinessException {
        Scanner sc = scannerThreadLocal.get();
        CookieUtil.getCookie();
        File backupDir = new File(BilibiliBackup.PATH_PREFIX);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            throw new BusinessException("备份文件为空");
        }
        File[] userFiles = backupDir.listFiles();
        if (userFiles == null || userFiles.length == 0) {
            throw new BusinessException("备份文件为空");
        }

        Arrays.sort(userFiles, Comparator.comparingLong(File::lastModified));
        log.info(String.format("总共有%s个备份文件，请输入前面的数字选择对应的备份", userFiles.length));
        for (int i = 0; i < userFiles.length; i++) {
            log.info(String.format("[%s]-[%s]", i, userFiles[i].getName()));
        }
        int pos;
        while (true) {
            pos = Integer.parseInt(sc.nextLine());
            if (pos < 0 || pos > userFiles.length - 1) {
                log.info("输入错误，请重新输入");
            } else {
                break;
            }
        }
        String readJsonDir = BilibiliBackup.PATH_PREFIX + userFiles[pos].getName() + File.separator;
        
        List<ServiceBuilder> items = ItemChoiceUtil.getServices();
        while (items.isEmpty()) {
            log.info("未选择任何项目，请重新选择");
            items = ItemChoiceUtil.getServices();
        }

        Upper upper = new BilibiliRestore(items, readJsonDir,new User(userCookieThreadLocal.get()), new DefaultUserInfoCallback()).start();
        log.info(String.format("成功%s[%s]", buTypeThreadLocal.get().getCnName(), upper.getName()));
        return null;
    }

    @Override
    public BuType getRequestType() {
        return BuType.RESTORE;
    }
}
