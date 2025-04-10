package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.bean.BuCallback;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.baseapp.business.IBusinessType;
import io.github.hzhilong.bilibili.backup.app.bean.BusinessResult;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 备份的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class BackupRunnable extends BackupRestoreRunnable {

    public BackupRunnable(Window parent, String appIconPath, OkHttpClient client, SavedUser user, LinkedHashSet<BackupRestoreItem> backupRestoreItems, BuCallback<Void> buCallback) {
        super(parent, appIconPath, client, user, backupRestoreItems, null, buCallback);
    }

    @Override
    protected List<BusinessResult> runService(BackupRestoreItem item, BackupRestoreService service) throws BusinessException {
        return service.backup();
    }

    @Override
    protected IBusinessType getBusinessType() {
        return BusinessType.BACKUP;
    }
}
