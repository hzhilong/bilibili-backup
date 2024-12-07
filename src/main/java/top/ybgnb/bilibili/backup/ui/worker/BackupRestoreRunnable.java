package top.ybgnb.bilibili.backup.ui.worker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreItem;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @ClassName BackupRestoreRunnable
 * @Description
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public abstract class BackupRestoreRunnable extends BaseRunnable {

    @Getter
    protected SavedUser user;
    protected BuCallback<Void> buCallback;
    protected BackupRestoreService currBackupRestoreService;
    @Getter
    protected LinkedHashMap<BackupRestoreItem, BackupRestoreService> backupRestoreItemServices;
    @Getter
    protected String backupDirPath;
    protected User apiUser;

    public BackupRestoreRunnable(OkHttpClient client, SavedUser user, LinkedHashSet<BackupRestoreItem> backupRestoreItems, String backupDirPath, BuCallback<Void> buCallback) {
        super(client);
        this.client = client;
        this.user = user;
        this.buCallback = buCallback;
        this.backupRestoreItemServices = new LinkedHashMap<>(backupRestoreItems.size());
        if (StringUtils.notEmpty(backupDirPath)) {
            this.backupDirPath = backupDirPath;
        } else {
            this.backupDirPath = String.format("%s%s_%s_%s/", AppConstant.BACKUP_PATH_PREFIX, user.getName(), user.getMid(),
                    new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        }
        this.apiUser = new User(user.getCookie());
        for (BackupRestoreItem item : backupRestoreItems) {
            backupRestoreItemServices.put(item, item.getServiceBuilder().build(this.client, this.apiUser, this.backupDirPath));
        }
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
        if (currBackupRestoreService != null) {
            currBackupRestoreService.setInterrupt(interrupt);
        }
    }

    protected abstract void runService(BackupRestoreItem item, BackupRestoreService service) throws BusinessException;

    @Override
    public void run() {
        boolean onceSuccessful = false;
        // 执行各个项目
        Iterator<Map.Entry<BackupRestoreItem, BackupRestoreService>> iterator = backupRestoreItemServices.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BackupRestoreItem, BackupRestoreService> next = iterator.next();
            BackupRestoreItem item = next.getKey();
            BackupRestoreService service = next.getValue();
            try {
                currBackupRestoreService = service;
                runService(item, currBackupRestoreService);
                onceSuccessful = true;
            } catch (Exception e) {
                if (e instanceof BusinessException) {
                    log.info("操作失败，{}\n", e.getMessage());
                } else {
                    log.error("操作失败，{}\n", e.getMessage(), e);
                }
            }
            if (interrupt) {
                log.info("已中断任务");
                break;
            }
        }
        if (buCallback != null) {
            if (interrupt) {
                buCallback.interrupt();
            } else {
                if (onceSuccessful) {
                    log.info("操作成功！");
                    buCallback.success(null);
                } else {
                    log.info("操作失败！");
                    buCallback.fail("操作失败！");
                }
            }
        }
    }
}
