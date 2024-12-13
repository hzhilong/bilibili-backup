package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.gui.worker.BaseRunnable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

/**
 * 工具箱的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public abstract class ToolRunnable<S extends BaseService, D> extends BaseRunnable {

    protected final SavedUser user;

    private final ToolBuCallback<D> buCallback;

    private S service;

    public ToolRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<D> buCallback) {
        super(client);
        this.user = user;
        this.buCallback = buCallback;
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
        if (service != null) {
            service.setInterrupt(interrupt);
        }
    }

    protected abstract S getService();

    protected abstract D runService(S service) throws BusinessException;

    @Override
    public void run() {
        boolean onceSuccessful = false;
        D result = null;
        try {
            service = getService();
            result = runService(service);
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
                        buCallback.success(result);
                    } else {
                        log.info("操作失败！");
                        buCallback.fail("操作失败！");
                    }
                }
            }
        }
    }

}
