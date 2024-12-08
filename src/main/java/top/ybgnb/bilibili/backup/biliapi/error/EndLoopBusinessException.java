package top.ybgnb.bilibili.backup.biliapi.error;

import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.BuResult;

/**
 * @ClassName EndLoopBusinessException
 * @Description 需跳出循环的业务异常
 * @Author hzhilong
 * @Time 2024/12/8
 * @Version 1.0
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
