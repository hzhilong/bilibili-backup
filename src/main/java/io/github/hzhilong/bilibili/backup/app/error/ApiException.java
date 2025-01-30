package io.github.hzhilong.bilibili.backup.app.error;

import io.github.hzhilong.base.bean.BuResult;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;

/**
 * 接口请求异常
 *
 * @author hzhilong
 * @version 1.0
 */
public class ApiException extends BusinessException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(BuResult<?> result) {
        super(String.format("%s(%s)", result.getMsg(), result.getCode()));
    }

    public ApiException(String message, ApiResult<?> apiResult) {
        super(String.format("%s，%s(%s)", message, apiResult.getMessage(), apiResult.getCode()));
    }

    public ApiException(ApiResult<?> apiResult) {
        super(String.format("%s(%s)", apiResult.getMessage(), apiResult.getCode()));
    }
}
