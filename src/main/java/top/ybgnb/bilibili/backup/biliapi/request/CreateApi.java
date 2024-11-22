package top.ybgnb.bilibili.backup.biliapi.request;

import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.util.Map;

/**
 * @ClassName CreateApi
 * @Description 创建接口
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
public class CreateApi<D> extends BaseApi<D> {

    public CreateApi(OkHttpClient client, User user, String url, Class<?>... dataClasses) {
        super(client, user, url, true, dataClasses);
    }

    public CreateApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<?>... dataClasses) {
        super(client, user, url, addQueryParams, true, dataClasses);
    }

    public ApiResult<D> create(Map<String, String> formParams) throws BusinessException {
        formParams.put("csrf", user.getBili_jct());
        return apiPost(formParams);
    }

}
