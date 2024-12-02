package top.ybgnb.bilibili.backup.ui.worker;

import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;

/**
 * @ClassName BaseRunnable
 * @Description 工作线程基类
 * @Author hzhilong
 * @Time 2024/11/30
 * @Version 1.0
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
