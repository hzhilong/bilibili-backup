package top.ybgnb.bilibili.backup.ui.worker;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public BackupRunnable(OkHttpClient client, SavedUser user, LinkedHashSet<ServiceBuilder> serviceBuilders, BuCallback<Void> buCallback) {
        super(client, user, serviceBuilders, buCallback);
    }

    @Override
    public void run() {
        String path = String.format("%s%s_%s_%s/", AppConstant.BACKUP_PATH_PREFIX, user.getName(), user.getMid(),
                new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        boolean onceSuccessful = false;
        // 执行各个项目
        for (ServiceBuilder item : serviceBuilders) {
            try {
                currBackupRestoreService = item.build(client, new User(user.getCookie()), path);
                currBackupRestoreService.backup();
                onceSuccessful = true;
            } catch (BusinessException ex) {
                log.info("操作失败，{}\n", ex.getMessage());
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
