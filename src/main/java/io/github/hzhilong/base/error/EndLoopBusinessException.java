package io.github.hzhilong.base.error;

import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.base.bean.BuResult;

/**
 * 需跳出循环的业务异常
 *
 * @author hzhilong
 * @version 1.0
 */
public class EndLoopBusinessException extends BusinessException{

    public EndLoopBusinessException(ApiResult<?> apiResult) {
        super(apiResult);
    }

    public EndLoopBusinessException(String message) {
        super(message);
    }

    public EndLoopBusinessException(String message, ApiResult<?> apiResult) {
        super(message, apiResult);
    }

    public EndLoopBusinessException(BuResult<?> result) {
        super(result);
    }
}
