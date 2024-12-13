package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.bean.BuCallback;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.api.service.BackupRestoreService;

import java.util.LinkedHashSet;

/**
 * 备份的线程
 *
 * @author hzhilong
 * @version 1.0
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
