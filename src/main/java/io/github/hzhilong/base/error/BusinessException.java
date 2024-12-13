package io.github.hzhilong.base.error;

import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.base.bean.BuResult;

/**
 * 业务异常
 *
 * @author hzhilong
 * @version 1.0
 */
public class BusinessException extends Exception {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(BuResult<?> result) {
        super(String.format("%s(%s)", result.getMsg(), result.getCode()));
    }

    public BusinessException(String message, ApiResult<?> apiResult) {
        super(String.format("%s，%s(%s)", message, apiResult.getMessage(), apiResult.getCode()));
    }

    public BusinessException(ApiResult<?> apiResult) {
        super(String.format("%s(%s)", apiResult.getMessage(), apiResult.getCode()));
    }

}
