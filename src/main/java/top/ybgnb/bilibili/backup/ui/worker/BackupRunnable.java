package top.ybgnb.bilibili.backup.ui.worker;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreItem;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;

import java.util.LinkedHashSet;

/**
 * @ClassName BackupRunnable
 * @Description 备份线程
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public class BackupRunnable extends BackupRestoreRunnable {

    public BackupRunnable(OkHttpClient client, SavedUser user, LinkedHashSet<BackupRestoreItem> backupRestoreItems, BuCallback<Void> buCallback) {
        super(client, user, backupRestoreItems, null, buCallback);
    }

    @Override
    protected void runService(BackupRestoreItem item, BackupRestoreService service) throws BusinessException {
        service.backup();
    }
}
