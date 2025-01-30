package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.LoginResult;
import io.github.hzhilong.bilibili.backup.api.bean.QRCode;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.UpperInfoCallback;
import io.github.hzhilong.base.error.BusinessException;

import java.util.List;

/**
 * 登录
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class LoginService extends BaseService {

    protected OkHttpClient client;

    public LoginService(OkHttpClient client) {
        super(client, null);
        this.client = client;
    }

    /**
     * 生成登录二维码
     */
    public QRCode generateQRCode() throws BusinessException {
        ApiResult<QRCode> apiResult =
                new BaseApi<QRCode>(client, null, "https://passport.bilibili.com/x/passport-login/web/qrcode/generate", false, QRCode.class).apiGet();
        if (apiResult.isSuccess()) {
            return apiResult.getData();
        } else {
            throw new ApiException(apiResult);
        }
    }

    public String login(QRCode qrCode) throws BusinessException {
        BaseApi.ApiResponse<LoginResult> response = new BaseApi<LoginResult>(client, null,
                "https://passport.bilibili.com/x/passport-login/web/qrcode/poll",
                queryParams -> queryParams.put("qrcode_key", qrCode.getQrcodeKey()),
                false, LoginResult.class).apiGetWithResponse();
        ApiResult<LoginResult> apiResult = response.getApiResult();
        if (apiResult.isSuccess() && apiResult.getData().getCode() == 0) {
            List<String> cookies = response.getHeaders().values("Set-Cookie");
            if (!cookies.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String c : cookies) {
                    if (!StringUtils.isEmpty(c)) {
                        sb.append(c, 0, c.indexOf(";") + 1);
                        sb.append(" ");
                    }
                }
                return perfectCookie(sb.toString());
            } else {
                throw new ApiException(apiResult);
            }
        } else if (86038 != apiResult.getCode()) {
            return "";
        } else {
            throw new ApiException(apiResult);
        }
    }

    private String perfectCookie(String cookie) throws BusinessException {
        ApiResult<JSONObject> apiResult = new BaseApi<JSONObject>(client, user, "https://api.bilibili.com/x/frontend/finger/spi",
                false, JSONObject.class).apiGet();
        if (apiResult.isFail()) {
            throw new ApiException("获取buvid3失败", apiResult);
        }
        JSONObject data = apiResult.getData();
        return String.format("%sbuvid3=%s; buvid4=%s; ", cookie, data.getString("b_3"), data.getString("b_4"));
    }

    public Upper getUpper(User user, UpperInfoCallback upperInfoCallback) throws BusinessException {
        log.info("正在获取用户信息，请稍候...");
        ApiResult<Upper> userInfo = new BaseApi<Upper>(client, new User(user.getCookie()),
                "https://api.bilibili.com/x/space/myinfo", true, Upper.class).apiGet();
        if (userInfo.isFail()) {
            log.error(userInfo.getMessage());
            if (upperInfoCallback != null) {
                upperInfoCallback.fail(user);
            }
            throw new BusinessException("获取当前用户信息失败");
        }
        if (upperInfoCallback != null) {
            upperInfoCallback.success(userInfo.getData());
        }
        return userInfo.getData();
    }
}
