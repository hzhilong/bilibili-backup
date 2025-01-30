package io.github.hzhilong.bilibili.backup.api.request;

import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.list.ListData;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.user.User;

import java.util.List;

/**
 * 列表接口
 *
 * @author hzhilong
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
public class ListApi<D, L> extends BaseApi<D> {

    protected Class<?> dataClass;

    protected Class<L> listItemClass;

    public ListApi(OkHttpClient client, User user, String url, Class<L> listItemClass) {
        this(client, user, url, null, null, listItemClass);
    }

    public ListApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<L> listItemClass) {
        this(client, user, url, addQueryParams, null, listItemClass);
    }

    public ListApi(OkHttpClient client, User user, String url, Class<?> dataClass, Class<L> listItemClass) {
        this(client, user, url, null, dataClass, listItemClass);
    }

    public ListApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<?> dataClass, Class<L> listItemClass) {
        super(client, user, url, addQueryParams, true, (Class<?>) null);
        this.dataClass = dataClass;
        this.listItemClass = listItemClass;
        if (dataClass == null) {
            dataClasses = new Class[]{ListData.class, listItemClass};
        } else {
            dataClasses = new Class[]{dataClass, listItemClass};
        }
    }

    public List<L> getList() throws BusinessException {
        ApiResult<D> apiResult = apiGet();
        if (apiResult.isSuccess()) {
            D resultData = apiResult.getData();
            if (resultData != null) {
                if (ListData.class.isAssignableFrom(this.dataClasses[0])) {
                    return ((ListData) resultData).getList();
                }
                return (List<L>) resultData;
            }
        }
        throw new ApiException(apiResult);
    }

}
