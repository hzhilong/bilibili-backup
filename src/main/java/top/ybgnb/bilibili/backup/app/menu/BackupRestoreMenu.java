package top.ybgnb.bilibili.backup.app.menu;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;
import top.ybgnb.bilibili.backup.app.utils.MenuUtil;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.BangumiService;
import top.ybgnb.bilibili.backup.biliapi.service.BlackService;
import top.ybgnb.bilibili.backup.biliapi.service.FavCollectedService;
import top.ybgnb.bilibili.backup.biliapi.service.FavOpusesService;
import top.ybgnb.bilibili.backup.biliapi.service.FavoritesService;
import top.ybgnb.bilibili.backup.biliapi.service.FollowingService;
import top.ybgnb.bilibili.backup.biliapi.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.biliapi.service.ToViewService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * @ClassName BackupRestoreMenu
 * @Description 备份还原菜单
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
@Slf4j
public class BackupRestoreMenu {

    /**
     * 选择服务项目
     *
     * @param sc
     * @return
     */
    public static List<ServiceBuilder> chooseServiceItems(Scanner sc) {
        List<ServiceBuilder> builders = new ArrayList<>();
        do {
            chooseServiceItem(sc, builders, "关注", FollowingService::new);
            chooseServiceItem(sc, builders, "我的追番/追剧", BangumiService::new);
            chooseServiceItem(sc, builders, "收藏的专栏", FavOpusesService::new);
            chooseServiceItem(sc, builders, "收藏的视频合集", FavCollectedService::new);
            chooseServiceItem(sc, builders, "稍后再看", ToViewService::new);
            chooseServiceItem(sc, builders, "收藏夹", FavoritesService::new);
            chooseServiceItem(sc, builders, "黑名单", BlackService::new);
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
     *
     * @param sc
     * @return
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
