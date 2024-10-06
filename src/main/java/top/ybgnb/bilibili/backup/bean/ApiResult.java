package top.ybgnb.bilibili.backup.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.Headers;

/**
 * @ClassName ApiResult
 * @Description
 * @Author hzhilong
 * @Time 2024/9/20
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class ApiResult<D> {
    int code;
    String message;
    D data;

    public boolean _isSuccess() {
        return code == 0;
    }

    public boolean _isFail() {
        return code != 0;
    }
}
