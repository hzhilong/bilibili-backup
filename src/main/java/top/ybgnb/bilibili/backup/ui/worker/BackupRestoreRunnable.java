package top.ybgnb.bilibili.backup.ui.worker;

import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.biliapi.service.ServiceBuilder;

import java.util.LinkedHashSet;

/**
 * @ClassName BackupRestoreRunnable
 * @Description
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
public abstract class BackupRestoreRunnable extends BaseRunnable {

    protected SavedUser user;
    protected LinkedHashSet<ServiceBuilder> serviceBuilders;
    protected BuCallback<Void> buCallback;
    protected BackupRestoreService currBackupRestoreService;

    public BackupRestoreRunnable(OkHttpClient client, SavedUser user, LinkedHashSet<ServiceBuilder> serviceBuilders, BuCallback<Void> buCallback) {
        super(client);
        this.client = client;
        this.user = user;
        this.serviceBuilders = serviceBuilders;
        this.buCallback = buCallback;
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
        if (currBackupRestoreService != null) {
            currBackupRestoreService.setInterrupt(interrupt);
        }
    }
}
