package top.ybgnb.bilibili.backup.biliapi.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.SessionPageData;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.AddQueryParams;
import top.ybgnb.bilibili.backup.biliapi.request.ModifyApi;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;

import java.util.HashMap;
import java.util.List;


/**
 * @ClassName MessageService
 * @Description 我的消息
 * @Author hzhilong
 * @Time 2024/10/3
 * @Version 1.0
 */
@Slf4j
public class MessageService extends BaseService {

    public MessageService(OkHttpClient client, User user) {
        super(client, user);
    }

    public void readAllSession() throws BusinessException {
        log.info("获取[我的消息]...");
        List<JSONObject> allSession = new PageApi<>(client, user, "https://api.vc.bilibili.com/session_svr/v1/session_svr/get_sessions",
                addQueryParamsGetAllSeSSion(), SessionPageData.class, JSONObject.class)
                .getAllData((allData, queryParams) -> {
                    if (ListUtil.notEmpty(allData)) {
                        queryParams.put("end_ts", String.valueOf(allData.get(allData.size() - 1).getLong("session_ts")));
                    }
                });
        int sessionCount = allSession.size();
        log.info("已获取{}条消息...", sessionCount);
        if (sessionCount < 1) {
            return;
        }
        for (int i = 0; i < sessionCount; i++) {
            log.info("解析中...{}%", (i + 1) * 100 / sessionCount);
            JSONObject session = allSession.get(i);
            Integer unreadCount = session.getInteger("unread_count");
            if (unreadCount > 0) {
                log.info("存在未读消息：{}", session.getJSONObject("last_msg").getJSONObject("content"));
                ApiResult<Object> apiResult = new ModifyApi<Object>(client, user,
                        "https://api.vc.bilibili.com/session_svr/v1/session_svr/update_ack", Object.class).modify(
                        new HashMap<String, String>() {{
                            put("talker_id", String.valueOf(session.getLong("talker_id")));
                            put("session_type", String.valueOf(session.getInteger("session_type")));
                            put("ack_seqno", String.valueOf(session.getLong("ack_seqno")));
                            put("build", "0");
                            put("mobi_app", "web");
                            put("csrf_token", user.getBili_jct());
                        }});
                if (apiResult._isSuccess()) {
                    log.info("已读成功");
                } else {
                    log.info("已读失败{}", apiResult.getMessage());
                }
            }
        }
        log.info("执行完成");
    }

    @NotNull
    private static AddQueryParams addQueryParamsGetAllSeSSion() {
        return queryParams -> {
            queryParams.put("session_type", "1");
            queryParams.put("group_fold", "1");
            queryParams.put("unfollow_fold", "0");
            queryParams.put("sort_rule", "2");
            queryParams.put("build", "0");
            queryParams.put("mobi_app", "web");
            queryParams.put("web_location", "333.1296");
        };
    }
}
