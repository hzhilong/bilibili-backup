package top.ybgnb.bilibili.backup.biliapi.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.page.PageCallback;
import top.ybgnb.bilibili.backup.biliapi.bean.page.PageData;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    public final static int MAX_DELAY_TIME = 5 * 1000;

    @Getter
    @Setter
    private int page;

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

    protected PageCallback pageCallback;

    private final Random random;

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
        random = new Random();
    }

    @Override
    public void addQueryParams(Map<String, String> queryParams) {
        queryParams.put("pn", String.valueOf(this.getPage()));
        queryParams.put("ps", "20");
    }

    public interface SetNextPage<D> {
        void setParams(D pageData, Map<String, String> queryParams);
    }

    public List<L> getAllData() throws BusinessException {
        return getAllData(null);
    }

    public List<L> getAllData(SetNextPage<D> setNextPage) throws BusinessException {
        D pageData = getAllPageData(setNextPage);
        if (pageData == null) {
            return null;
        }
        return pageData._getList();
    }

    public D getAllPageData() throws BusinessException {
        return getAllPageData(null);
    }

    public D getAllPageData(SetNextPage<D> setNextPage) throws BusinessException {
        List<L> list = new ArrayList<>();
        AddQueryParams baseAddQueryParams = null;
        if (setNextPage != null) {
            baseAddQueryParams = this.addQueryParams;
        }
        int page = 1;
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
            if (apiResult._isSuccess()) {
                pageData = apiResult.getData();
                if (pageData != null && pageData._getList() != null) {
                    list.addAll(pageData._getList());
                    if (pageCallback != null) {
                        pageCallback.page(this.getPage(), ListUtil.getSize(pageData._getList()), list.size());
                    }
                    pageData._setList(list);
                    if (pageData.hasMore(list.size())) {
                        page++;
                        sleep(page);
                        continue;
                    }
                }
            } else {
                throw new BusinessException(apiResult);
            }
            break;
        }
        if (pageData != null) {
            pageData._setList(list);
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

    private void sleep(int page) {
        try {
            Thread.sleep((1000 + 1000 * Integer.toString(page).length() + random.nextInt(3000)) % MAX_DELAY_TIME);
        } catch (InterruptedException ignored) {
        }
    }
}
