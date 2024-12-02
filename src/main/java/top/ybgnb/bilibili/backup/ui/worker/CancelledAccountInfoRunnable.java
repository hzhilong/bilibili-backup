package top.ybgnb.bilibili.backup.ui.worker;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.CancelledAccountInfo;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.impl.CancelledAccountService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

/**
 * @ClassName CancelledAccountInfoRunnable
 * @Description 获取已注销账号信息的线程
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public class CancelledAccountInfoRunnable extends BaseRunnable {

    private String uid;

    private BuCallback<CancelledAccountInfo> buCallback;

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
        } catch (BusinessException e) {
            log.info("操作失败，{}\n", e.getMessage());
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
