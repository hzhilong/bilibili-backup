package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.bean.BuCallback;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.bean.CancelledAccountInfo;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.service.impl.CancelledAccountService;
import io.github.hzhilong.bilibili.backup.api.user.User;

/**
 * 获取已注销账号信息的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class CancelledAccountInfoRunnable extends BaseRunnable {

    private final String uid;

    private final BuCallback<CancelledAccountInfo> buCallback;

    public CancelledAccountInfoRunnable(OkHttpClient client, String uid, BuCallback<CancelledAccountInfo> buCallback) {
        super(client);
        this.uid = uid;
        this.buCallback = buCallback;
    }

    @Override
    public void run() {
        boolean onceSuccessful = false;
        CancelledAccountInfo info = null;
        try {
            info = new CancelledAccountService(client, new User(uid)).getInfo();
            onceSuccessful = true;
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                log.info("操作失败，{}\n", e.getMessage());
            } else {
                log.error("操作失败，{}\n", e.getMessage(), e);
            }
        } finally {
            if (buCallback != null) {
                if (interrupt) {
                    log.info("已中断任务");
                    buCallback.interrupt();
                } else {
                    if (onceSuccessful) {
                        log.info("操作成功！");
                        buCallback.success(info);
                    } else {
                        log.info("操作失败！");
                        buCallback.fail("操作失败！");
                    }
                }
            }
        }
    }

}
