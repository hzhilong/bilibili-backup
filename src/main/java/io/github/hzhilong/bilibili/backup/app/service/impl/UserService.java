package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.UserCard;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.Map;


/**
 * 账号
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class UserService extends BaseService {

    public UserService(OkHttpClient client, User user) {
        super(client, user);
    }

    public void openAutoReplyMsg(boolean open) throws BusinessException {
        String optName = open ? "打开" : "关闭";
        log.info("正在强制{}私信自动回复功能...", optName);
        ApiResult<JSONObject> apiResult = new ModifyApi<JSONObject>(client, user, "https://api.vc.bilibili.com/link_setting/v1/link_setting/set").modify(
                new HashMap<String, String>() {{
                    put("keys_reply", open ? "1" : "0");
                }});
        if (apiResult.isSuccess()) {
            log.info("{}成功{}", optName, open ? "，B站消息中心的菜单即可看到【自动回复】" : "");
        } else {
            log.info("{}失败，{}", optName, apiResult.getMessage());
        }
    }

    /**
     * 获取用户名片信息
     *
     * @param uid
     * @return
     */
    public UserCard getCard(String uid, boolean ignoreResult) throws BusinessException {
        log.info("正在获取用户{}的名片信息，请稍候...", uid);
        ApiResult<UserCard> apiResult = new BaseApi<UserCard>(client, new User(user.getCookie()),
                "https://api.bilibili.com/x/web-interface/card",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("mid", uid);
                    }
                }, true, UserCard.class).apiGet();
        if (!ignoreResult && apiResult.isFail()) {
            log.error(apiResult.getMessage());
            throw new BusinessException("获取当前用户信息失败");
        }
        return apiResult.getData();
    }

}
