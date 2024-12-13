package io.github.hzhilong.bilibili.backup.gui.worker;

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
}
