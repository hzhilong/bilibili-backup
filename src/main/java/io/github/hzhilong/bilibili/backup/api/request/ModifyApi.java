package io.github.hzhilong.bilibili.backup.api.request;

import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.user.User;

import java.util.Map;

/**
 * 修改接口
 *
 * @author hzhilong
 * @version 1.0
 */
public class ModifyApi<D> extends BaseApi<D> {

    public ModifyApi(OkHttpClient client, User user, String url, Class<?>... dataClasses) {
        super(client, user, url, true, dataClasses);
    }

    public ModifyApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<?>... dataClasses) {
        super(client, user, url, addQueryParams, true, dataClasses);
    }

    public ApiResult<D> modify(Map<String, String> formParams) throws BusinessException {
        formParams.put("csrf", user.getBiliJct());
        return apiPost(formParams);
    }

    @Override
    @Deprecated
    public ApiResult<D> apiPost(Map<String, String> formParams) throws BusinessException {
        return super.apiPost(formParams);
    }
}
