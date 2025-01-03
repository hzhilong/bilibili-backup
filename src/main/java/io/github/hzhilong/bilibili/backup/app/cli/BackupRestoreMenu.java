package io.github.hzhilong.bilibili.backup.app.cli;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.bean.ServiceBuilder;
import io.github.hzhilong.bilibili.backup.app.utils.MenuUtil;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * 备份还原菜单
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class BackupRestoreMenu extends BaseMenu {

    /**
     * 选择服务项目
     */
    public static List<ServiceBuilder> chooseServiceItems(Scanner sc) {
        List<ServiceBuilder> builders = new ArrayList<>();
        do {
            for (BackupRestoreItem item : BackupRestoreItem.values()) {
                chooseServiceItem(sc, builders, item.getName(), item.getServiceBuilder());
            }
            if (builders.isEmpty()) {
                log.info("未选择任何项目，请重新选择！");
            }
        } while (builders.isEmpty());
        return builders;
    }

    private static void chooseServiceItem(Scanner sc, List<ServiceBuilder> builders, String name, ServiceBuilder builder) {
        log.info("是否包括[{}]？", name);
        log.info("输入 y：是\t输入其他：否");
        String nextLine = sc.nextLine();
        if ("Y".equals(nextLine) || "y".equals(nextLine)) {
            builders.add(builder);
        }
    }

    /**
     * 选择备份目录
     */
    public static String chooseBackupDir(Scanner sc) throws BusinessException {
        File backupDir = new File(AppConstant.BACKUP_PATH_PREFIX);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            throw new BusinessException("备份文件为空");
        }
        File[] userFiles = backupDir.listFiles();
        if (userFiles == null || userFiles.length == 0) {
            throw new BusinessException("备份文件为空");
        }

        Arrays.sort(userFiles, Comparator.comparingLong(File::lastModified).reversed());
        log.info("总共有{}个备份文件，请输入前面的数字选择对应的备份", userFiles.length);
        for (int i = 0; i < userFiles.length; i++) {
            log.info("[{}]-[{}]", i, userFiles[i].getName());
        }
        int pos = MenuUtil.checkInputPos(userFiles.length, sc.nextLine());
        return AppConstant.BACKUP_PATH_PREFIX + userFiles[pos].getName() + File.separator;
    }
}
