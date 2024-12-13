package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口请求结果
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class ApiResult<D> {
    int code;
    String message;
    D data;

    public boolean isSuccess() {
        return code == 0;
    }

    public boolean isFail() {
        return code != 0;
    }
}
