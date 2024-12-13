package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.impl.MessageService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

/**
 * 已读所有消息的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class ReadAllSessionRunnable extends ToolRunnable<MessageService, Void> {

    public ReadAllSessionRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback) {
        super(client, user, buCallback);
    }

    @Override
    protected MessageService getService() {
        return new MessageService(client, new User(user.getCookie()));
    }

    @Override
    protected Void runService(MessageService service) throws BusinessException {
        service.readAllSession();
        return null;
    }
}
