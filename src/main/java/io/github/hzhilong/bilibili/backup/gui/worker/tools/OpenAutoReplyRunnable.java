package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

/**
 * 设置私信自动回复功能的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class OpenAutoReplyRunnable extends ToolRunnable<UserService, Void> {

    private boolean flag;

    public OpenAutoReplyRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback, boolean flag) {
        super(client, user, buCallback);
        this.flag = flag;
    }

    @Override
    protected UserService getService() {
        return new UserService(client, new User(user.getCookie()));
    }

    @Override
    protected Void runService(UserService service) throws BusinessException {
        service.openAutoReplyMsg(flag);
        return null;
    }
}
