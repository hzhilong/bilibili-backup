package top.ybgnb.bilibili.backup.biliapi.error;

import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.BuResult;

/**
 * @ClassName BusinessException
 * @Description 业务异常
 * @Author hzhilong
 * @Time 2023/3/28
 * @Version 1.0
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
