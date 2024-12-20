package io.github.hzhilong.bilibili.backup.app.service;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.user.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.Random;

/**
 * 服务基类
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@Slf4j
public abstract class BaseService {
    /**
     * 最长的延迟时间 毫秒
     */
    public final static int MAX_DELAY_TIME = 5 * 1000;

    protected OkHttpClient client;

    protected final User user;

    /**
     * 是否中断
     */
    @Setter
    @Getter
    protected boolean interrupt;

    private final Random random;

    public BaseService(OkHttpClient client, User user) {
        this.client = client;
        this.user = user;
        this.random = new Random();
    }

    /**
     * 是否进行鉴权
     */
    protected User signUser() {
        if (user != null && !user.isCancelledAccount()) {
            return user;
        }
        return null;
    }

    /**
     * 处理中断
     */
    protected void handleInterrupt() throws BusinessException {
        if (isInterrupt()) {
            throw new BusinessException("任务中断");
        }
    }

    public void sleep(int count) {
        try {
            Thread.sleep((1000 + 1000 * Integer.toString(count).length() + random.nextInt(2000)) % MAX_DELAY_TIME);
        } catch (InterruptedException ignored) {
        }
    }

}
