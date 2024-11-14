package top.ybgnb.bilibili.backup.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.bean.ApiResult;
import top.ybgnb.bilibili.backup.bean.SessionPageData;
import top.ybgnb.bilibili.backup.constant.URLConstant;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.request.AddQueryParams;
import top.ybgnb.bilibili.backup.request.ModifyApi;
import top.ybgnb.bilibili.backup.request.PageApi;
import top.ybgnb.bilibili.backup.user.User;
import top.ybgnb.bilibili.backup.utils.ListUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @ClassName MessageService
 * @Description 消息
 * @Author hzhilong
 * @Time 2024/10/3
 * @Version 1.0
 */
@Slf4j
public class MessageService extends BaseService {

    public MessageService(OkHttpClient client, User user) {
        super(client, user);
    }


    // todo 优化点 readAllSession 结构、可加进度条
    public void readAllSession() throws BusinessException {
        log.info("获取会话...");
        List<JSONObject> allSession = new PageApi<SessionPageData, JSONObject>(client, user,
                URLConstant.GET_SESSIONS, new AddQueryParams() {
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
        }, SessionPageData.class, JSONObject.class).getAllData(new PageApi.SetNextPage<JSONObject>() {
            @Override
            public void setParams(List<JSONObject> allData, Map<String, String> queryParams) {
                if (ListUtil.notEmpty(allData)) {
                    queryParams.put("end_ts", String.valueOf(allData.get(allData.size() - 1).getLong("session_ts")));
                }
            }
        });

        // todo 优化点 readAllSession 感觉反复get unread_count逻辑不太对
        for (JSONObject session : allSession) {
            Integer unreadCount = session.getInteger("unread_count");
            if (unreadCount > 0) {
                log.info("存在未读消息：" + session.getJSONObject("last_msg").getString("content"));
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
        log.info("完成.");
    }
}
