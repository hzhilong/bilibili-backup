package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.bean.BuCallback;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.bean.BackupRestoreResult;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 还原的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class RestoreRunnable extends BackupRestoreRunnable {

    public RestoreRunnable(OkHttpClient client, SavedUser user, LinkedHashSet<BackupRestoreItem> backupRestoreItems, String backupDirPath, BuCallback<Void> buCallback) {
        super(client, user, backupRestoreItems, backupDirPath.endsWith(File.separator) ? backupDirPath : (backupDirPath + File.separator), buCallback);
    }

    @Override
    protected BusinessType getBusinessType() {
        return BusinessType.RESTORE;
    }

    @Override
    protected List<BackupRestoreResult> runService(BackupRestoreItem item, BackupRestoreService service) throws BusinessException {
        return service.restore();
    }
}
