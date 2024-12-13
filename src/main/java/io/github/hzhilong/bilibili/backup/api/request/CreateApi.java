package io.github.hzhilong.bilibili.backup.api.request;

import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.user.User;

import java.util.Map;

/**
 * 创建接口
 *
 * @author hzhilong
 * @version 1.0
 */
public class CreateApi<D> extends BaseApi<D> {

    public CreateApi(OkHttpClient client, User user, String url, Class<?>... dataClasses) {
        super(client, user, url, true, dataClasses);
    }

    public CreateApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<?>... dataClasses) {
        super(client, user, url, addQueryParams, true, dataClasses);
    }

    public ApiResult<D> create(Map<String, String> formParams) throws BusinessException {
        formParams.put("csrf", user.getBiliJct());
        return apiPost(formParams);
    }

}
