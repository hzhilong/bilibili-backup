package top.ybgnb.bilibili.backup.error;

import top.ybgnb.bilibili.backup.bean.ApiResult;
import top.ybgnb.bilibili.backup.bean.BuResult;

/**
 * @ClassName BusinessException
 * @Description 业务异常
 * @Author hzhilong
 * @Time 2023/3/28
 * @Version 1.0
 */
public class BusinessException extends Exception {

    private boolean isEndLoop = false;

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

    public BusinessException(String message, boolean isEndLoop) {
        super(message);
        this.isEndLoop = isEndLoop;
    }

    public BusinessException(BuResult<?> result, boolean isEndLoop) {
        super(String.format("%s(%s)", result.getMsg(), result.getCode()));
        this.isEndLoop = isEndLoop;
    }

    public BusinessException(ApiResult<?> apiResult, boolean isEndLoop) {
        super(String.format("%s(%s)", apiResult.getMessage(), apiResult.getCode()));
        this.isEndLoop = isEndLoop;
    }

    public boolean isEndLoop() {
        return isEndLoop;
    }

    public void setEndLoop(boolean endLoop) {
        isEndLoop = endLoop;
    }
}
