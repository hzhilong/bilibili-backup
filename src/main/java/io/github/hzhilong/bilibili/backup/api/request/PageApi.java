package io.github.hzhilong.bilibili.backup.api.request;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.page.PageCallback;
import io.github.hzhilong.bilibili.backup.api.bean.page.PageData;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分页接口
 *
 * @author hzhilong
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class PageApi<D extends PageData<L>, L> extends BaseApi<D> {

    public final static int MAX_SIZE = -1;

    /**
     * 最长的延迟时间 毫秒
     */
    public final static int MAX_DELAY_TIME = 5 * 1000;

    @Getter
    @Setter
    private int page;

    @Setter
    @Getter
    private int pageSize;

    /**
     * 接口响应的数据类
     */
    protected Class<D> dataClass;

    /**
     * 分页子项数据类
     */
    protected Class<L> listItemClass;

    /**
     * 是否中断
     */
    @Setter
    @Getter
    protected boolean interrupt;

    /**
     * 分页查询回调方法
     */
    protected PageCallback pageCallback;

    public PageApi(OkHttpClient client, User user, String url, Class<L> listItemClass) {
        this(client, user, url, null, null, listItemClass, null);
    }

    public PageApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<L> listItemClass) {
        this(client, user, url, addQueryParams, null, listItemClass, null);
    }

    public PageApi(OkHttpClient client, User user, String url, Class<D> dataClass, Class<L> listItemClass) {
        this(client, user, url, null, dataClass, listItemClass, null);
    }

    public PageApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<D> dataClass, Class<L> listItemClass) {
        this(client, user, url, addQueryParams, dataClass, listItemClass, null);
    }

    public PageApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<D> dataClass, Class<L> listItemClass, PageCallback pageCallback) {
        super(client, user, url, addQueryParams, true, (Class<?>) null);
        this.dataClass = dataClass;
        this.listItemClass = listItemClass;
        if (dataClass == null) {
            dataClasses = new Class[]{PageData.class, listItemClass};
        } else {
            dataClasses = new Class[]{dataClass, listItemClass};
        }
        if (pageCallback == null) {
            this.pageCallback = (page, currPageSize, totalSize) -> log.info("已获取第{}页{}条数据，总数据量：{}", page, currPageSize, totalSize);
        } else {
            this.pageCallback = pageCallback;
        }
        this.pageSize = 20;
    }

    @Override
    public void addQueryParams(Map<String, String> queryParams) {
        queryParams.put("pn", String.valueOf(this.getPage()));
        queryParams.put("ps", String.valueOf(this.getPageSize()));
    }

    public interface SetNextPage<D> {
        void setParams(D pageData, Map<String, String> queryParams);
    }

    public List<L> getAllData() throws BusinessException {
        return getAllData(1);
    }

    public List<L> getAllData(int startPage) throws BusinessException {
        return getAllData(startPage, MAX_SIZE);
    }

    public List<L> getAllData(int startPage, int maxSize) throws BusinessException {
        return getAllData(null, startPage, maxSize);
    }

    public List<L> getAllData(SetNextPage<D> setNextPage) throws BusinessException {
        return getAllData(setNextPage, 1);
    }

    public List<L> getAllData(SetNextPage<D> setNextPage, int startPage) throws BusinessException {
        return getAllData(setNextPage, startPage, MAX_SIZE);
    }

    public List<L> getAllData(SetNextPage<D> setNextPage, int startPage, int maxSize) throws BusinessException {
        D pageData = getAllPageData(setNextPage, startPage, maxSize);
        if (pageData == null) {
            return null;
        }
        return pageData.getList();
    }

    public D getAllPageData() throws BusinessException {
        return getAllPageData(1);
    }

    public D getAllPageData(int startPage) throws BusinessException {
        return getAllPageData(null, startPage);
    }

    public D getAllPageData(SetNextPage<D> setNextPage) throws BusinessException {
        return getAllPageData(setNextPage, 1);
    }

    public D getAllPageData(SetNextPage<D> setNextPage, int startPage) throws BusinessException {
        return getAllPageData(setNextPage, startPage, MAX_SIZE);
    }

    public D getAllPageData(SetNextPage<D> setNextPage, int startPage, int maxSize) throws BusinessException {
        List<L> list = new ArrayList<>();
        AddQueryParams baseAddQueryParams = null;
        if (setNextPage != null) {
            baseAddQueryParams = this.addQueryParams;
        }
        int page = startPage;
        D pageData = null;
        while (true) {
            handleInterrupt();
            this.setPage(page);
            if (setNextPage != null) {
                AddQueryParams finalBaseAddQueryParams = baseAddQueryParams;
                D finalPageData = pageData;
                this.addQueryParams = queryParams -> {
                    if (finalBaseAddQueryParams != null) {
                        finalBaseAddQueryParams.addQueryParams(queryParams);
                    }
                    setNextPage.setParams(finalPageData, queryParams);
                };
            }
            ApiResult<D> apiResult = apiGet();
            if (setNextPage != null) {
                this.addQueryParams = baseAddQueryParams;
            }
            if (apiResult.isSuccess()) {
                pageData = apiResult.getData();
                if (pageData != null && pageData.getList() != null) {
                    int currCount = pageData.getList().size();
                    list.addAll(pageData.getList());
                    if (pageCallback != null) {
                        pageCallback.page(page, ListUtil.getSize(pageData.getList()), list.size());
                    }
                    pageData.setList(list);
                    if (ListUtil.notEmpty(list) && maxSize > 0 && list.size() >= maxSize) {
                        log.info("暂停获取，目前总数：{}，允许的最大内容数：{}", list.size(), maxSize);
                        break;
                    } else if (currCount > 0 && pageData.hasMore(list.size())) {
                        page++;
                        sleep(page);
                        continue;
                    }
                }
            } else {
                throw new ApiException(apiResult);
            }
            break;
        }
        if (pageData != null) {
            pageData.setList(list);
        }
        return pageData;
    }

    /**
     * 处理中断
     */
    protected void handleInterrupt() throws BusinessException {
        if (isInterrupt()) {
            throw new BusinessException("任务中断");
        }
    }

}
