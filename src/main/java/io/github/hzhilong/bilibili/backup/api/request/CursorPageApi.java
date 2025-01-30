package io.github.hzhilong.bilibili.backup.api.request;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.page.PageCallback;
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
 * 滑动分页请求
 *
 * @author hzhilong
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CursorPageApi<D> extends BaseApi<List<D>> {

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
     * 是否中断
     */
    @Setter
    @Getter
    protected boolean interrupt;

    /**
     * 分页查询回调方法
     */
    protected PageCallback pageCallback;

    public CursorPageApi(OkHttpClient client, User user, String url, Class<D> dataClass) {
        this(client, user, url, null, dataClass, null);
    }

    public CursorPageApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<D> dataClass) {
        this(client, user, url, addQueryParams, dataClass, null);
    }

    public CursorPageApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, Class<D> dataClass, PageCallback pageCallback) {
        super(client, user, url, addQueryParams, true, (Class<?>) null);
        this.dataClass = dataClass;
        if (pageCallback == null) {
            this.pageCallback = (page, currPageSize, totalSize) -> log.info("已获取第{}页{}条数据，总数据量：{}", page, currPageSize, totalSize);
        } else {
            this.pageCallback = pageCallback;
        }
        this.pageSize = 20;
    }

    @Override
    public void addQueryParams(Map<String, String> queryParams) {
    }

    public interface SetCursor<D> {
        void setParams(List<D> list, Map<String, String> queryParams);
    }

    public List<D> getAllData(SetCursor<D> setCursor) throws BusinessException {
        List<D> result = new ArrayList<>();
        AddQueryParams baseAddQueryParams = null;
        if (setCursor != null) {
            baseAddQueryParams = this.addQueryParams;
        }
        int page = 1;
        List<D> pageData = null;
        while (true) {
            handleInterrupt();
            this.setPage(page);
            if (setCursor != null) {
                AddQueryParams finalBaseAddQueryParams = baseAddQueryParams;
                List<D> finalPageData = pageData;
                this.addQueryParams = queryParams -> {
                    if (finalBaseAddQueryParams != null) {
                        finalBaseAddQueryParams.addQueryParams(queryParams);
                    }
                    setCursor.setParams(finalPageData, queryParams);
                };
            }
            ApiResult<List<D>> apiResult = apiGet();
            if (setCursor != null) {
                this.addQueryParams = baseAddQueryParams;
            }
            if (apiResult.isSuccess()) {
                pageData = apiResult.getData();
                if (pageData != null && !pageData.isEmpty()) {
                    result.addAll(pageData);
                    if (pageCallback != null) {
                        pageCallback.page(page, ListUtil.getSize(pageData), result.size());
                    }
                    page++;
                    sleep(page);
                    continue;
                }
            } else {
                throw new ApiException(apiResult);
            }
            break;
        }
        return result;
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
