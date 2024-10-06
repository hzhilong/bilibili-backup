package top.ybgnb.bilibili.backup.request;

import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.bean.ApiResult;
import top.ybgnb.bilibili.backup.bean.ListData;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.user.User;

import java.util.List;

/**
 * @ClassName ListAllApi
 * @Description 列表接口
 * @Author hzhilong
 * @Time 2024/9/20
 * @Version 1.0
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
        if (apiResult._isSuccess()) {
            D resultData = apiResult.getData();
            if (resultData != null) {
                if (ListData.class.isAssignableFrom(this.dataClasses[0])) {
                    return ((ListData) resultData).getList();
                }
                return (List<L>) resultData;
            }
        }
        throw new BusinessException(apiResult);
    }

}
