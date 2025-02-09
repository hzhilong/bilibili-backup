package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.impl.MessageService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.LinkedHashSet;

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
    private MessageService messageService;

    public SessionRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback, int type) {
        super(client, user, buCallback);
        this.type = type;
    }

    @Override
    protected void newServices(LinkedHashSet<MessageService> services) {
        messageService = new MessageService(client, new User(user.getCookie()));
        services.add(messageService);
    }

    @Override
    protected Void runTool() throws BusinessException {
        if (type == TYPE_READ_ALL_SESSION) {
            messageService.readAllSession();
        } else if (type == TYPE_DELETE_ALL_SESSION) {
            messageService.readAllSession(true);
        } else if (type == TYPE_DELETE_ALL_SYS_MSG) {
            messageService.deleteAllSysMsg();
        }
        return null;
    }
}
