package top.ybgnb.bilibili.backup.biliapi.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.LoginResult;
import top.ybgnb.bilibili.backup.biliapi.bean.QRCode;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.BaseApi;
import top.ybgnb.bilibili.backup.biliapi.utils.StringUtils;

import java.util.List;

/**
 * @ClassName LoginService
 * @Description
 * @Author hzhilong
 * @Time 2024/9/28
 * @Version 1.0
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
     *
     * @return
     * @throws BusinessException
     */
    public QRCode generateQRCode() throws BusinessException {
        ApiResult<QRCode> apiResult =
                new BaseApi<QRCode>(client, null, "https://passport.bilibili.com/x/passport-login/web/qrcode/generate", false, QRCode.class).apiGet();
        if (apiResult._isSuccess()) {
            return apiResult.getData();
        } else {
            throw new BusinessException(apiResult);
        }
    }

    public String login(QRCode qrCode) throws BusinessException {
        BaseApi.ApiResponse<LoginResult> response = new BaseApi<LoginResult>(client, null,
                "https://passport.bilibili.com/x/passport-login/web/qrcode/poll",
                queryParams -> queryParams.put("qrcode_key", qrCode.getQrcode_key()),
                false, LoginResult.class).apiGetWithResponse();
        ApiResult<LoginResult> apiResult = response.getApiResult();
        if (apiResult._isSuccess() && apiResult.getData().getCode() == 0) {
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
                throw new BusinessException(apiResult);
            }
        } else if (86038 != apiResult.getCode()) {
            return "";
        } else {
            throw new BusinessException(apiResult);
        }
    }

    private String perfectCookie(String cookie) throws BusinessException {
        ApiResult<JSONObject> apiResult = new BaseApi<JSONObject>(client, user, "https://api.bilibili.com/x/frontend/finger/spi",
                false, JSONObject.class).apiGet();
        if (apiResult._isFail()) {
            throw new BusinessException("获取buvid3失败", apiResult);
        }
        JSONObject data = apiResult.getData();
        return String.format("%sbuvid3=%s; buvid4=%s; ", cookie, data.getString("b_3"), data.getString("b_4"));
    }
}
