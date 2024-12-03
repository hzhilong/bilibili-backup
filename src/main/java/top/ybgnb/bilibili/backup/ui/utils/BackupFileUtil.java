package top.ybgnb.bilibili.backup.ui.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreItem;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;
import top.ybgnb.bilibili.backup.biliapi.utils.StringUtils;
import top.ybgnb.bilibili.backup.ui.bean.BackupDir;
import top.ybgnb.bilibili.backup.ui.bean.BackupFile;
import top.ybgnb.bilibili.backup.ui.config.AppData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName BackupFileUtil
 * @Description 备份文件工具
 * @Author hzhilong
 * @Time 2024/11/29
 * @Version 1.0
 */
@Slf4j
public class BackupFileUtil {

    private static final AppData APP_DATA = AppData.getInstance();

    public static final HashMap<String, String> cn2En = new HashMap<String, String>() {{
        put("我的追番", "Bangumi1");
        put("我的追剧", "Bangumi2");
        put("黑名单", "Black");
        put("收藏的视频合集", "FavCollected");
        put("收藏的专栏", "FavOpuses");
        put("收藏夹", "Favorites");
        put("创建的收藏夹", "CreatedFavorites");
        put("粉丝", "Follower");
        put("关注分组", "RelationTags");
        put("关注", "Following");
        put("稍后再看", "ToView");
        put("稍后再看", "ToView");
    }};

    public static String getEnName(String cnName) throws BusinessException {
        return cn2En.getOrDefault(cnName, cnName);
    }

    public static String getPathEnName(String cnPathName) {
        if (StringUtils.isEmpty(cnPathName)) {
            return "";
        }
        return cn2En.getOrDefault(cnPathName, cnPathName) + "/";
    }

    /**
     * 获取所有的备份文件信息
     *
     * @return
     */
    public static List<BackupDir> getBackupFiles() {
        List<BackupDir> backupDirs = new ArrayList<>();
        File backupRootDir = new File(AppConstant.BACKUP_PATH_PREFIX);
        if (!backupRootDir.exists() || !backupRootDir.isDirectory()) {
            return backupDirs;
        }
        File[] userFiles = backupRootDir.listFiles();
        if (userFiles == null || userFiles.length == 0) {
            return backupDirs;
        }
        Arrays.sort(userFiles, Comparator.comparingLong(File::lastModified).reversed());
        for (File dir : userFiles) {
            BackupDir backupDir = new BackupDir();
            backupDir.setDirFile(dir);
            backupDir.setName(dir.getName());

            List<BackupFile> backupFiles = new ArrayList<>();
            for (BackupRestoreItem item : BackupRestoreItem.values()) {
                try {
                    BackupFile backupFile = new BackupFile();
                    backupFile.setItem(item);
                    BackupRestoreService service = item.getServiceBuilder().build(null, null, null);
                    backupFile.setCount(service.getBackupCount(dir));
                    backupFile.setSegment(APP_DATA.getBackupSegment(dir.getName(), item.getName()) > 0);
                    backupFiles.add(backupFile);
                } catch (Exception ignored) {

                }
            }
            backupDir.setBackupFiles(backupFiles);
            backupDirs.add(backupDir);
        }
        return backupDirs;
    }

    public static void deleteBackupDir(BackupDir dir) {
        File file = dir.getDirFile();
        if (file != null && file.isDirectory()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                log.error("删除备份文件失败。", e);
            }
        }
    }

    public static List<BackupRestoreItem> getBackupRestoreItems(BackupDir backupDir) {
        List<BackupRestoreItem> list = new ArrayList<>(BackupRestoreItem.values().length);
        if (backupDir == null || ListUtil.isEmpty(backupDir.getBackupFiles())) {
            return list;
        }
        for (BackupFile backupFile : backupDir.getBackupFiles()) {
            list.add(backupFile.getItem());
        }
        return list;
    }

}
