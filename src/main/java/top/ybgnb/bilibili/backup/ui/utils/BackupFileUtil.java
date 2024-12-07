package top.ybgnb.bilibili.backup.ui.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreItem;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;
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
import java.util.Map;

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

    public static final Map<BackupRestoreItem, BackupRestoreService> getBackupCount = new HashMap<>(BackupRestoreItem.values().length);

    static {
        for (BackupRestoreItem item : BackupRestoreItem.values()) {
            BackupRestoreService service = item.getServiceBuilder().build(null, null, null);
            getBackupCount.put(item, service);
        }
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
                    backupFile.setCount(getBackupCount.get(item).getBackupCount(dir));
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
