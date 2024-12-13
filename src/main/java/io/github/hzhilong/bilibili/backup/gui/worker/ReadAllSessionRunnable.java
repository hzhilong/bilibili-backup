package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.bean.BuCallback;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.service.impl.MessageService;
import io.github.hzhilong.bilibili.backup.api.user.User;

/**
 * 已读所有消息的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class ReadAllSessionRunnable extends BaseRunnable {

    private final SavedUser user;
    private final BuCallback<Void> buCallback;

    private MessageService messageService;

    public ReadAllSessionRunnable(OkHttpClient client, SavedUser user, BuCallback<Void> buCallback) {
        super(client);
        this.user = user;
        this.buCallback = buCallback;
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
        if (messageService != null) {
            messageService.setInterrupt(interrupt);
        }
    }

    @Override
    public void run() {
        boolean onceSuccessful = false;
        try {
            messageService = new MessageService(client, new User(user.getCookie()));
            messageService.readAllSession();
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
                        buCallback.success(null);
                    } else {
                        log.info("操作失败！");
                        buCallback.fail("操作失败！");
                    }
                }
            }
        }
    }

}
