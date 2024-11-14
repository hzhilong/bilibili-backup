package top.ybgnb.bilibili.backup.app;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.bean.ApiResult;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.request.BaseApi;
import top.ybgnb.bilibili.backup.request.ThrottlingInterceptor;
import top.ybgnb.bilibili.backup.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.userInfoCallback.UserInfoCallback;
import top.ybgnb.bilibili.backup.user.User;

import java.util.List;

/**
 * @ClassName BaseApp
 * @Description
 * @Author hzhilong
 * @Time 2024/10/3
 * @Version 1.0
 */
@Slf4j
public abstract class BaseApp {

    protected List<ServiceBuilder> serviceBuilders;

    protected User user;

    protected UserInfoCallback userInfoCallback;

    protected OkHttpClient client;

    public BaseApp(List<ServiceBuilder> serviceBuilders, User user, UserInfoCallback userInfoCallback) {
        this.serviceBuilders = serviceBuilders;
        this.user = user;
        this.userInfoCallback = userInfoCallback;
        this.client = new OkHttpClient.Builder().addInterceptor(
                new ThrottlingInterceptor(1000)).build();
    }

    public abstract Upper start() throws BusinessException;

    public Upper getUpper() throws BusinessException {
        ApiResult<Upper> userInfo = new BaseApi<Upper>(client, this.user,
                "https://api.bilibili.com/x/space/myinfo", true, Upper.class).apiGet();
        if (userInfo._isFail()) {
            log.error(userInfo.getMessage());
            if (userInfoCallback != null) {
                userInfoCallback.fail(this.user);
            }
            throw new BusinessException("获取当前用户信息失败");
        }
        if (userInfoCallback != null) {
            userInfoCallback.success(userInfo.getData());
        }
        return userInfo.getData();
    }
}
