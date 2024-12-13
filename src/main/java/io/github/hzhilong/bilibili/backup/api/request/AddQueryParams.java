package io.github.hzhilong.bilibili.backup.api.request;

import java.util.Map;

/**
 * 添加查询参数
 *
 * @author hzhilong
 * @version 1.0
 */
public interface AddQueryParams {
    void addQueryParams(Map<String, String> queryParams);
}
