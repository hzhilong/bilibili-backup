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
public class SessionRunnable extends ToolRunnable<MessageService, Void> {

    public static final int TYPE_READ_ALL_SESSION = 1;
    public static final int TYPE_DELETE_ALL_SESSION = 2;
    public static final int TYPE_DELETE_ALL_SYS_MSG = 3;

    private int type;

    public SessionRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback, int type) {
        super(client, user, buCallback);
        this.type = type;
    }

    @Override
    protected MessageService getService() {
        return new MessageService(client, new User(user.getCookie()));
    }

    @Override
    protected Void runService(MessageService service) throws BusinessException {
        if (type == TYPE_READ_ALL_SESSION) {
            service.readAllSession();
        } else if (type == TYPE_DELETE_ALL_SESSION) {
            service.readAllSession(true);
        } else if (type == TYPE_DELETE_ALL_SYS_MSG) {
            service.deleteAllSysMsg();
        }
        return null;
    }
}
