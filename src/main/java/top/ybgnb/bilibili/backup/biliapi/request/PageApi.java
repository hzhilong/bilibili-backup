package top.ybgnb.bilibili.backup.biliapi.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.PageData;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PageApi
 * @Description 分页接口
 * @Author hzhilong
 * @Time 2024/9/20
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class PageApi<D extends PageData<L>, L> extends BaseApi<D> {

    @Getter
    @Setter
    private int page;

    protected Class<D> dataClass;

    protected Class<L> listItemClass;

    public PageApi(OkHttpClient client, User user, String url, Class<L> listItemClass) {
        this(client, user, url, null, null, listItemClass);
    }

    public PageApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<L> listItemClass) {
        this(client, user, url, addQueryParams, null, listItemClass);
    }

    public PageApi(OkHttpClient client, User user, String url, Class<D> dataClass, Class<L> listItemClass) {
        this(client, user, url, null, dataClass, listItemClass);
    }

    public PageApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<D> dataClass, Class<L> listItemClass) {
        super(client, user, url, addQueryParams, true, (Class<?>) null);
        this.dataClass = dataClass;
        this.listItemClass = listItemClass;
        if (dataClass == null) {
            dataClasses = new Class[]{PageData.class, listItemClass};
        } else {
            dataClasses = new Class[]{dataClass, listItemClass};
        }
    }

    @Override
    public void addQueryParams(Map<String, String> queryParams) {
        queryParams.put("pn", String.valueOf(this.getPage()));
        queryParams.put("ps", "20");
    }

    public interface SetNextPage<L> {
        void setParams(List<L> allData, Map<String, String> queryParams);
    }

    public List<L> getAllData() throws BusinessException {
        return getAllData(null);
    }

    public List<L> getAllData(SetNextPage<L> setNextPage) throws BusinessException {
        List<L> list = new ArrayList<>();
        AddQueryParams baseAddQueryParams = null;
        if (setNextPage != null) {
            baseAddQueryParams = this.addQueryParams;
        }
        int page = 1;
        while (true) {
            this.setPage(page);
            if (setNextPage != null) {
                AddQueryParams finalBaseAddQueryParams = baseAddQueryParams;
                this.addQueryParams = new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        if (finalBaseAddQueryParams != null) {
                            finalBaseAddQueryParams.addQueryParams(queryParams);
                        }
                        setNextPage.setParams(list, queryParams);
                    }
                };
            }
            ApiResult<D> apiResult = apiGet();
            if (setNextPage != null) {
                this.addQueryParams = baseAddQueryParams;
            }
            if (apiResult._isSuccess()) {
                D data = apiResult.getData();
                if (data != null && data._getList() != null) {
                    list.addAll(data._getList());
                    if (data.hasMore(list.size())) {
                        page++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {

                        }
                        continue;
                    }
                }
            } else {
                throw new BusinessException(apiResult.getMessage());
            }
            break;
        }
        return list;
    }

    public D getAllPageData() throws BusinessException {
        return getAllPageData(null);
    }
    public D getAllPageData(SetNextPage<L> setNextPage) throws BusinessException {
        List<L> list = new ArrayList<>();
        AddQueryParams baseAddQueryParams = null;
        if (setNextPage != null) {
            baseAddQueryParams = this.addQueryParams;
        }
        int page = 1;
        D page1Data = null;
        while (true) {
            this.setPage(page);
            if (setNextPage != null) {
                AddQueryParams finalBaseAddQueryParams = baseAddQueryParams;
                this.addQueryParams = new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        if (finalBaseAddQueryParams != null) {
                            finalBaseAddQueryParams.addQueryParams(queryParams);
                        }
                        setNextPage.setParams(list, queryParams);
                    }
                };
            }
            ApiResult<D> apiResult = apiGet();
            if (setNextPage != null) {
                this.addQueryParams = baseAddQueryParams;
            }
            if (apiResult._isSuccess()) {
                D data = apiResult.getData();
                if (page1Data == null) {
                    page1Data = data;
                }
                if (data != null && data._getList() != null) {
                    list.addAll(data._getList());
                    if (data.hasMore(list.size())) {
                        page++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {

                        }
                        continue;
                    }
                }
            } else {
                throw new BusinessException(apiResult);
            }
            break;
        }
        if (page1Data != null) {
            page1Data._setList(list);
        }
        return page1Data;
    }

}
