package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.error.BusinessException;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;

/**
 * 工作线程基类
 *
 * @author hzhilong
 * @version 1.0
 */
public abstract class BaseRunnable implements Runnable {

    protected OkHttpClient client;

    @Setter
    @Getter
    protected boolean interrupt;

    public BaseRunnable(OkHttpClient client) {
        this.client = client;
    }

    /**
     * 处理中断
     */
    protected void handleInterrupt() throws BusinessException {
        if (isInterrupt()) {
            throw new BusinessException("任务中断");
        }
    }

    public void sleep(int sec) {
        try {
            Thread.sleep(1000L * sec);
        } catch (InterruptedException ignored) {
        }
    }
}
