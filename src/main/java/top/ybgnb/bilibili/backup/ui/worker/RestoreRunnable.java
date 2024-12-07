package top.ybgnb.bilibili.backup.ui.worker;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreItem;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;

import java.io.File;
import java.util.LinkedHashSet;

/**
 * @ClassName RestoreRunnable
 * @Description 还原线程
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public class RestoreRunnable extends BackupRestoreRunnable {

    public RestoreRunnable(OkHttpClient client, SavedUser user, LinkedHashSet<BackupRestoreItem> backupRestoreItems, String backupDirPath, BuCallback<Void> buCallback) {
        super(client, user, backupRestoreItems, backupDirPath.endsWith(File.separator) ? backupDirPath : (backupDirPath + File.separator), buCallback);
    }

    @Override
    protected void runService(BackupRestoreItem item, BackupRestoreService service) throws BusinessException {
        service.restore();
    }
}
