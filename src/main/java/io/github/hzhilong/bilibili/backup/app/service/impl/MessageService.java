package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.page.SessionPageData;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.error.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 我的消息
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class MessageService extends BaseService {

    public MessageService(OkHttpClient client, User user) {
        super(client, user);
    }

    public void readAllSession() throws BusinessException {
        log.info("获取[我的消息]...");
        List<JSONObject> allSession = new PageApi<>(client, user,
                "https://api.vc.bilibili.com/session_svr/v1/session_svr/get_sessions",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("session_type", "1");
                        queryParams.put("group_fold", "1");
                        queryParams.put("unfollow_fold", "0");
                        queryParams.put("sort_rule", "2");
                        queryParams.put("build", "0");
                        queryParams.put("mobi_app", "web");
                        queryParams.put("web_location", "333.1296");
                    }
                }, SessionPageData.class, JSONObject.class)
                .getAllData((pageData, queryParams) -> {
                    if (pageData != null) {
                        List<JSONObject> allData = pageData.getList();
                        if (ListUtil.notEmpty(allData)) {
                            queryParams.put("end_ts", String.valueOf(allData.get(allData.size() - 1).getLong("session_ts")));
                        }
                    }
                });
        int sessionCount = allSession.size();
        log.info("已获取{}条消息...", sessionCount);
        if (sessionCount < 1) {
            return;
        }
        for (int i = 0; i < sessionCount; i++) {
            handleInterrupt();
            log.info("解析中...{}%", (i + 1) * 100 / sessionCount);
            JSONObject session = allSession.get(i);
            Integer unreadCount = session.getInteger("unread_count");
            if (unreadCount > 0) {
                log.info("存在未读消息：{}", session.getJSONObject("last_msg").getJSONObject("content"));
                ApiResult<Object> apiResult = new ModifyApi<>(client, user,
                        "https://api.vc.bilibili.com/session_svr/v1/session_svr/update_ack", Object.class).modify(
                        new HashMap<String, String>() {{
                            put("talker_id", String.valueOf(session.getLong("talker_id")));
                            put("session_type", String.valueOf(session.getInteger("session_type")));
                            put("ack_seqno", String.valueOf(session.getLong("ack_seqno")));
                            put("build", "0");
                            put("mobi_app", "web");
                            put("csrf_token", user.getBiliJct());
                        }});
                if (apiResult.isSuccess()) {
                    log.info("已读成功");
                } else {
                    log.info("已读失败{}", apiResult.getMessage());
                }
            }
        }
    }

}
