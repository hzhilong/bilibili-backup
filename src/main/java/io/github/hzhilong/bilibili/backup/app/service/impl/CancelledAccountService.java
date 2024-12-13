package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.CancelledAccountInfo;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.api.user.User;

import java.util.Map;

/**
 * @ClassName CancelledAccountService
 * @Description 已注销账号
 * @Author hzhilong
 * @Time 2024/11/30
 * @Version 1.0
 */
@Slf4j
public class CancelledAccountService extends BaseService {

    public CancelledAccountService(OkHttpClient client, User user) {
        super(client, user);
    }

    public CancelledAccountInfo getInfo() throws BusinessException {
        log.info("获取用户[{}]信息中...", user.getUid());
        ApiResult<JSONObject> apiResult = new BaseApi<JSONObject>(client, null, "https://api.bilibili.com/x/relation/stat", new AddQueryParams() {
            @Override
            public void addQueryParams(Map<String, String> queryParams) {
                queryParams.put("vmid", user.getUid());
            }
        }, false, JSONObject.class).apiGet();
        if (apiResult.isSuccess()) {
            JSONObject data = apiResult.getData();
            CancelledAccountInfo info = new CancelledAccountInfo();
            info.setUid(user.getUid());
            info.setFollowerCount(data.getInteger("follower"));
            info.setFollowingCount(data.getInteger("following"));
            return info;
        } else {
            throw new BusinessException("查询该uid用户信息失败");
        }
    }

}
