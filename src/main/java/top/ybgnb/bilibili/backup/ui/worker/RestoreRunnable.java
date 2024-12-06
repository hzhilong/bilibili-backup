package top.ybgnb.bilibili.backup.ui.worker;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.biliapi.user.User;

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

    private String backupDirPath;

    public RestoreRunnable(OkHttpClient client, SavedUser user, LinkedHashSet<ServiceBuilder> serviceBuilders, String backupDirPath, BuCallback<Void> buCallback) {
        super(client, user, serviceBuilders, buCallback);
        this.backupDirPath = backupDirPath.endsWith("/") ? backupDirPath : (backupDirPath + "/");
    }

    @Override
    public void run() {
        boolean onceSuccessful = false;
        // 执行各个项目
        for (ServiceBuilder item : serviceBuilders) {
            try {
                currBackupRestoreService = item.build(client, new User(user.getCookie()), backupDirPath);
                currBackupRestoreService.restore();
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
