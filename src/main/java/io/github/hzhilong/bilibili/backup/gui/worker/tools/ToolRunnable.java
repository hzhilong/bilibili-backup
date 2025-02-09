package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.gui.worker.BaseRunnable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.LinkedHashSet;

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

    private LinkedHashSet<S> services;

    public ToolRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<D> buCallback) {
        super(client);
        this.user = user;
        this.buCallback = buCallback;
        this.services = new LinkedHashSet<>();
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
        if (services != null) {
            for (S service : services) {
                service.setInterrupt(interrupt);
            }
        }
    }

    protected abstract void newServices(LinkedHashSet<S> services);

    protected abstract D runTool() throws BusinessException;

    @Override
    public void run() {
        boolean onceSuccessful = false;
        D result = null;
        try {
            newServices(services);
            result = runTool();
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
