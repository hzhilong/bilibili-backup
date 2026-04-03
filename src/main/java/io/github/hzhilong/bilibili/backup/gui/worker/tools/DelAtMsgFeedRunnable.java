package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.baseapp.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.app.service.impl.MessageService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 清空[@ 我的]消息
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class DelAtMsgFeedRunnable extends ToolRunnable<BaseService, Void> implements NeedContext {

    private MessageService messageService;

    @Setter
    private Window parentWindow;
    @Setter
    private String appIconPath;

    public DelAtMsgFeedRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback) {
        super(client, user, buCallback);
    }

    @Override
    protected void newServices(LinkedHashSet<BaseService> services) {
        messageService = new MessageService(client, new User(user.getCookie()));
        services.add(messageService);
    }


    @Override
    protected Void runTool() throws BusinessException {
        List<JSONObject> all = messageService.getAllAtMsgFeed();
        log.info("已获取{}条被@的通知", all.size());

        int successCount = 0;
        for (JSONObject msg : all) {
            try {
                messageService.delAtMsgFeed(msg);
                log.info("已删除@通知 [{}]", msg.get("id"));
                successCount++;
            } catch (BusinessException e) {
                log.info("处理被@的通知失败：[{}]{}", msg, e.getMessage());
            }
        }
        log.info("成功处理{}条被@的通知", successCount);
        return null;
    }


}
