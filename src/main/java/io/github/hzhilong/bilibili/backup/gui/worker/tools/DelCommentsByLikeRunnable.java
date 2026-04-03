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
 * 通过点赞通知删除所有关联的评论，且在删除成功后同步删除通知消息
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class DelCommentsByLikeRunnable extends ToolRunnable<BaseService, Void> implements NeedContext {

    private MessageService messageService;

    @Setter
    private Window parentWindow;
    @Setter
    private String appIconPath;

    public DelCommentsByLikeRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback) {
        super(client, user, buCallback);
    }

    @Override
    protected void newServices(LinkedHashSet<BaseService> services) {
        messageService = new MessageService(client, new User(user.getCookie()));
        services.add(messageService);
    }

    @Override
    protected Void runTool() throws BusinessException {
        List<JSONObject> allLikeMsg = messageService.getAllLikeMsgFeed();
        log.info("已获取{}条点赞通知", allLikeMsg.size());
        int successCount = 0;
        for (JSONObject msg : allLikeMsg) {
            try {
                messageService.delCommentByLikeMsg(msg);
            } catch (BusinessException e) {
                continue;
            }
            // 确保已删除评论后再关闭并删除通知
            try {
                messageService.closeLikeMsgNotice(msg);
                messageService.delLikeMsgFeed(msg);
                log.info("已删除点赞通知 [{}]", msg.get("id"));
                successCount++;
            } catch (BusinessException e) {
                log.info("处理点赞通知失败：[{}]{}", msg, e.getMessage());
            }
        }
        log.info("成功处理{}条通知", successCount);
        return null;
    }
}
