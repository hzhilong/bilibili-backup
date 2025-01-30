package io.github.hzhilong.bilibili.backup.app.error;

import io.github.hzhilong.base.bean.BuResult;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;

/**
 * 需跳出循环的异常
 *
 * @author hzhilong
 * @version 1.0
 */
public class NeedEndLoopException extends ApiException {

    public NeedEndLoopException(String message) {
        super(message);
    }

    public NeedEndLoopException(BuResult<?> result) {
        super(result);
    }

    public NeedEndLoopException(String message, ApiResult<?> apiResult) {
        super(message, apiResult);
    }

    public NeedEndLoopException(ApiResult<?> apiResult) {
        super(apiResult);
    }
}
