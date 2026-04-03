package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.CommentDelParams;
import io.github.hzhilong.bilibili.backup.api.bean.page.CursorPageData2;
import io.github.hzhilong.bilibili.backup.api.bean.page.CursorPageData3;
import io.github.hzhilong.bilibili.backup.api.bean.page.CursorPageTotal;
import io.github.hzhilong.bilibili.backup.api.bean.page.SessionPageData;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.api.request.CursorPageApi;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * 我的消息
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class MessageService extends BaseService {
    CommentService commentService;

    PageApi<SessionPageData, JSONObject> pageApi;
    PageApi<CursorPageData2, JSONObject> likeMsgFeedPageApi;
    PageApi<CursorPageData3, JSONObject> replyMsgFeedPageApi;
    // 点赞通知
    static final String MSG_FEED_TYPE_LIKE = "0";
    // 回复通知
    static final String MSG_FEED_TYPE_REPLY = "1";
    // @ 通知
    static final String MSG_FEED_TYPE_AT = "2";

    public MessageService(OkHttpClient client, User user) {
        super(client, user);
        commentService = new CommentService(this.client, this.user);
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
                    log.info("  删除成功");
                } else {
                    log.info("  删除失败{}", apiResult.getMessage());
                }
                sleep(1);
            } else if (unreadCount > 0) {
                log.info("  存在未读消息");
                ApiResult<Object> apiResult = readSession(talkerId, sessionType, ackSeqno);
                if (apiResult.isSuccess()) {
                    log.info("  已读成功");
                } else {
                    log.info("  已读失败{}", apiResult.getMessage());
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
        if (likeMsgFeedPageApi != null) {
            likeMsgFeedPageApi.setInterrupt(interrupt);
        }
        if (replyMsgFeedPageApi != null) {
            replyMsgFeedPageApi.setInterrupt(interrupt);
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
        List<JSONObject> list = new CursorPageApi<JSONObject>(client, signUser(),
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
                .getAllData((list1, queryParams) -> {
                    if (ListUtil.notEmpty(list1)) {
                        queryParams.put("cursor", String.valueOf(list1.get(list1.size() - 1).get("cursor")));
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

    /**
     * 获取所有的点赞通知
     */
    public List<JSONObject> getAllLikeMsgFeed() throws BusinessException {
        likeMsgFeedPageApi = new PageApi(client, user,
                "https://api.bilibili.com/x/msgfeed/like",
                queryParams -> {
                    queryParams.put("build", "0");
                    queryParams.put("mobi_app", "web");
                    queryParams.put("web_location", "333.40164");
                }, CursorPageData2.class, JSONObject.class);
        List<JSONObject> likeMsgList = likeMsgFeedPageApi
                .getAllData((pageData, queryParams) -> {
                    if (pageData != null) {
                        CursorPageTotal total = pageData.getTotal();
                        if (total != null && ListUtil.notEmpty(total.getItems())) {
                            JSONObject last = total.getItems().get(total.getItems().size() - 1);
                            queryParams.put("id", String.valueOf(last.getLong("id")));
                            queryParams.put("like_time", String.valueOf(last.getLong("like_time")));
                        }
                    }
                });
        return likeMsgList;
    }

    /**
     * 获取所有的点赞通知
     */
    public List<JSONObject> getAllReplyMsgFeed() throws BusinessException {
        replyMsgFeedPageApi = new PageApi(client, user,
                "https://api.bilibili.com/x/msgfeed/reply",
                queryParams -> {
                    queryParams.put("build", "0");
                    queryParams.put("mobi_app", "web");
                    queryParams.put("web_location", "333.40164");
                }, CursorPageData3.class, JSONObject.class);
        List<JSONObject> list = replyMsgFeedPageApi
                .getAllData((pageData, queryParams) -> {
                    if (pageData != null && ListUtil.notEmpty(pageData.getItems())) {
                        JSONObject last = pageData.getItems().get(pageData.getItems().size() - 1);
                        queryParams.put("id", String.valueOf(last.getLong("id")));
                        queryParams.put("reply_time", String.valueOf(last.getLong("reply_time")));
                    }
                });
        return list;
    }

    /**
     * 获取所有的点赞通知
     */
    public List<JSONObject> getAllAtMsgFeed() throws BusinessException {
        replyMsgFeedPageApi = new PageApi(client, user,
                "https://api.bilibili.com/x/msgfeed/at",
                queryParams -> {
                    queryParams.put("build", "0");
                    queryParams.put("mobi_app", "web");
                    queryParams.put("web_location", "333.40164");
                }, CursorPageData3.class, JSONObject.class);
        List<JSONObject> list = replyMsgFeedPageApi
                .getAllData((pageData, queryParams) -> {
                    if (pageData != null && ListUtil.notEmpty(pageData.getItems())) {
                        JSONObject last = pageData.getItems().get(pageData.getItems().size() - 1);
                        queryParams.put("id", String.valueOf(last.getLong("id")));
                        queryParams.put("at_time", String.valueOf(last.getLong("at_time")));
                    }
                });
        return list;
    }

    /**
     * 批量关闭点赞通知（不再通知）
     *
     * @param list 点赞信息列表
     * @throws BusinessException
     */
    public void closeLikeMsgNotice(List<JSONObject> list) throws BusinessException {
        if (ListUtil.notEmpty(list)) {
            for (JSONObject msg : list) {
                this.closeLikeMsgNotice(msg);
            }
        }
    }

    /**
     * 关闭某条点赞通知（不再通知）
     */
    public void closeLikeMsgNotice(JSONObject msg) throws BusinessException {
        String msgId = msg.getString("id");
        this.setLikeMsgNoticeState(msgId, "1");
    }

    /**
     * 设置某条点赞通知的状态
     */
    public void setLikeMsgNoticeState(String msgId, String state) throws BusinessException {
        ApiResult<JSONObject> apiResult = new ModifyApi<JSONObject>(client, user,
                "https://api.bilibili.com/x/msgfeed/notice", JSONObject.class)
                .modify(new HashMap<String, String>() {{
                    put("tp", "0");
                    put("id", msgId);
                    put("notice_state", state);
                    put("build", "0");
                    put("mobi_app", "web");
                    put("platform", "web");
                }});
        if (apiResult.isFail()) {
            throw new ApiException(apiResult);
        }
    }

    /**
     * 删除某条互动的通知消息
     */
    public void delMsgFeed(String msgId, String tp) throws BusinessException {
        ApiResult<JSONObject> apiResult = new ModifyApi<JSONObject>(client, user,
                "https://api.bilibili.com/x/msgfeed/del", JSONObject.class)
                .modify(new HashMap<String, String>() {{
                    put("tp", tp);
                    put("id", msgId);
                    put("build", "0");
                    put("mobi_app", "web");
                    put("platform", "web");
                }});
        if (apiResult.isFail()) {
            throw new ApiException(apiResult);
        }
    }

    /**
     * 批量删除互动的通知消息
     */
    public void delMsgFeed(List<JSONObject> list, String tp) throws BusinessException {
        if (ListUtil.notEmpty(list)) {
            for (JSONObject msg : list) {
                this.delMsgFeed(String.valueOf(msg.getLong("id")), tp);
            }
        }
    }

    /**
     * 删除互动的点赞通知消息
     */
    public void delLikeMsgFeed(JSONObject msg) throws BusinessException {
        if (msg != null) {
            this.delMsgFeed(String.valueOf(msg.getLong("id")), MSG_FEED_TYPE_LIKE);
        }
    }

    /**
     * 批量删除互动的点赞通知消息
     */
    public void delLikeMsgFeed(List<JSONObject> list) throws BusinessException {
        if (ListUtil.notEmpty(list)) {
            for (JSONObject msg : list) {
                this.delLikeMsgFeed(msg);
            }
        }
    }

    /**
     * 删除互动的回复通知消息
     */
    public void delReplyMsgFeed(JSONObject msg) throws BusinessException {
        if (msg != null) {
            this.delMsgFeed(String.valueOf(msg.getLong("id")), MSG_FEED_TYPE_REPLY);
        }
    }

    /**
     * 批量删除互动的回复通知消息
     */
    public void delReplyMsgFeed(List<JSONObject> list) throws BusinessException {
        if (ListUtil.notEmpty(list)) {
            for (JSONObject msg : list) {
                this.delMsgFeed(String.valueOf(msg.getLong("id")), MSG_FEED_TYPE_REPLY);
            }
        }
    }

    /**
     * 删除互动的@通知消息
     */
    public void delAtMsgFeed(JSONObject msg) throws BusinessException {
        if (msg != null) {
            this.delMsgFeed(String.valueOf(msg.getLong("id")), MSG_FEED_TYPE_AT);
        }
    }

    /**
     * 根据点赞通知删除关联的评论
     */
    public void delCommentByLikeMsg(JSONObject msg) throws BusinessException {
        if (msg == null || !msg.containsKey("item")) throw new BusinessException("点赞通知为空");

        JSONObject item = msg.getJSONObject("item");

        if (StringUtils.isEmpty(item.getString("business"))) {
            return;
        }

        String title = item.getString("title");
        String rpid = item.getString("item_id");
        String native_uri = item.getString("native_uri");
        commentService.del(rpid, native_uri, title, null);
    }


    /**
     * 根据回复通知删除关联的评论
     *
     * @return
     */
    public CommentDelParams delCommentByReplyMsg(JSONObject msg, Set<String> deletedCache) throws BusinessException {
        if (msg == null || !msg.containsKey("item")) throw new BusinessException("回复通知为空");

        JSONObject item = msg.getJSONObject("item");

        if (StringUtils.isEmpty(item.getString("business"))) {
            return new CommentDelParams();
        }

        String title = item.getString("title");
        String rpid = item.getString("target_id");
        String native_uri = item.getString("native_uri");
        return commentService.del(rpid, native_uri, title, deletedCache);
    }
}
