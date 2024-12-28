package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.page.SessionPageData;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.api.request.CursorPageApi;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 我的消息
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class MessageService extends BaseService {

    PageApi<SessionPageData, JSONObject> pageApi;

    public MessageService(OkHttpClient client, User user) {
        super(client, user);
    }

    public void readAllSession() throws BusinessException {
        readAllSession(false);
    }

    public void readAllSession(boolean delete) throws BusinessException {
        log.info("获取[我的消息]...");
        pageApi = new PageApi<>(client, user,
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
                }, SessionPageData.class, JSONObject.class);
        List<JSONObject> allSession = pageApi
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
        String logNoFormat = StringUtils.getLogNoFormat(sessionCount);
        if (sessionCount < 1) {
            return;
        }
        for (int i = 0; i < sessionCount; i++) {
            handleInterrupt();
            JSONObject session = allSession.get(i);
            String title = getSessionTitle(session);
            Long talkerId = session.getLong("talker_id");
            Long ackSeqno = session.getLong("ack_seqno");
            Integer sessionType = session.getInteger("session_type");
            Integer unreadCount = session.getInteger("unread_count");

            log.info("{}{}", String.format(logNoFormat, i + 1), title);
            if (delete) {
                ApiResult<Object> apiResult = removeSession(talkerId, sessionType);
                if (apiResult.isSuccess()) {
                    log.info("{}删除成功", String.format(logNoFormat, i + 1));
                } else {
                    log.info("{}删除失败{}", String.format(logNoFormat, i + 1), apiResult.getMessage());
                }
                sleep(1);
            } else if (unreadCount > 0) {
                log.info("{}存在未读消息", String.format(logNoFormat, i + 1));
                ApiResult<Object> apiResult = readSession(talkerId, sessionType, ackSeqno);
                if (apiResult.isSuccess()) {
                    log.info("{}已读成功", String.format(logNoFormat, i + 1));
                } else {
                    log.info("{}已读失败{}", String.format(logNoFormat, i + 1), apiResult.getMessage());
                }
                sleep(1);
            }
        }
    }

    private ApiResult<Object> readSession(Long talkerId, Integer sessionType, Long ackSeqno) throws BusinessException {
        return new ModifyApi<>(client, user,
                "https://api.vc.bilibili.com/session_svr/v1/session_svr/update_ack", Object.class)
                .modify(
                        new HashMap<String, String>() {{
                            put("talker_id", String.valueOf(talkerId));
                            put("session_type", String.valueOf(sessionType));
                            put("ack_seqno", String.valueOf(ackSeqno));
                            put("build", "0");
                            put("mobi_app", "web");
                            put("csrf_token", user.getBiliJct());
                        }});
    }

    private String getSessionTitle(JSONObject session) {
        String title;
        try {
            title = session.getJSONObject("account_info").getString("name");
        } catch (Exception e) {
            JSONObject jsonContent = session.getJSONObject("last_msg").getJSONObject("content");
            if (jsonContent.containsKey("title")) {
                title = jsonContent.getString("title");
            } else if (jsonContent.containsKey("content")) {
                title = jsonContent.getString("content");
            } else if (jsonContent.containsKey("reply_content")) {
                title = jsonContent.getString("reply_content");
            } else {
                title = Objects.toString(jsonContent.toString());
            }
        }
        if (title == null) {
            title = "";
        }
        title = title.replaceAll("\n", " ");
        if (title.length() > 30) {
            title = title.substring(0, 30) + "...";
        }
        return title;
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        if (pageApi != null) {
            pageApi.setInterrupt(interrupt);
        }
        super.setInterrupt(interrupt);
    }

    private ApiResult<Object> removeSession(long talkerId, int sessionType) throws BusinessException {
        return new ModifyApi<>(client, user,
                "https://api.vc.bilibili.com/session_svr/v1/session_svr/remove_session", Object.class)
                .modify(
                        new HashMap<String, String>() {{
                            put("talker_id", String.valueOf(talkerId));
                            put("session_type", String.valueOf(sessionType));
                            put("build", "0");
                            put("mobi_app", "web");
                            put("csrf_token", user.getBiliJct());
                        }});
    }

    public List<JSONObject> getSysMessages() throws BusinessException {
        int pn = 1;
        List<JSONObject> list = new CursorPageApi<>(client, signUser(),
                "https://message.bilibili.com/x/sys-msg/query_notify_list",
                queryParams -> {
                    queryParams.put("page_size", "20");
                    queryParams.put("build", "0");
                    queryParams.put("mobi_app", "web");
                    queryParams.put("source_platform", "web");
                    queryParams.put("data_type", "web");
                    queryParams.put("has_up", "1");
                    queryParams.put("csrf", user.getBiliJct());
                }, JSONObject.class)
                .getAllData(new CursorPageApi.SetCursor<JSONObject>() {
                    @Override
                    public void setParams(List<JSONObject> list, Map<String, String> queryParams) {
                        if (ListUtil.notEmpty(list)) {
                            queryParams.put("cursor", String.valueOf(list.get(list.size() - 1).get("cursor")));
                        }
                    }
                });
        return list;
    }

    public void deleteAllSysMsg() throws BusinessException {
        List<JSONObject> sysMessages = getSysMessages();
        if (ListUtil.notEmpty(sysMessages)) {
            List<List<Long>> idsList = new ArrayList<>(10);
            List<List<Long>> stationIdsList = new ArrayList<>(10);
            for (JSONObject sysMessage : sysMessages) {
                List<List<Long>> temp;
                if (sysMessage.containsKey("is_station") && 1 == sysMessage.getInteger("is_station")) {
                    // 全站通知
                    temp = stationIdsList;
                } else {
                    // 系统通知
                    temp = idsList;
                }
                List<Long> last = temp.isEmpty() ? null : temp.get(temp.size() - 1);
                if (last == null || last.size() >= 10) {
                    last = new ArrayList<>(10);
                    temp.add(last);
                }
                last.add(sysMessage.getLong("id"));
            }
            for (List<Long> ids : idsList) {
                ApiResult<Object> apiResult = removeSysMessages(ids, new ArrayList<>());
                if (apiResult.isSuccess()) {
                    log.info("已删除{}条系统消息", ids.size());
                } else {
                    log.info("删除失败{}", apiResult.getMessage());
                }
                sleep(1);
            }
            for (List<Long> ids : stationIdsList) {
                ApiResult<Object> apiResult = removeSysMessages(new ArrayList<>(), ids);
                if (apiResult.isSuccess()) {
                    log.info("已删除{}条全站通知", ids.size());
                } else {
                    log.info("删除失败{}", apiResult.getMessage());
                }
                sleep(1);
            }
        }
    }

    public ApiResult<Object> removeSysMessages(List<Long> ids, List<Long> stationIds) throws BusinessException {
        JSONObject body = new JSONObject();
        body.put("type", 4);
        body.put("build", 0);
        body.put("station_ids", stationIds);
        body.put("mobi_app", "web");
        body.put("csrf", user.getBiliJct());
        body.put("ids", ids);
        return new BaseApi<>(client, user,
                "https://message.bilibili.com/x/sys-msg/del_notify_list",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("build", "0");
                        queryParams.put("mobi_app", "web");
                        queryParams.put("csrf", user.getBiliJct());
                    }
                }, true, Object.class)
                .apiPost(body.toJSONString());
    }
}
