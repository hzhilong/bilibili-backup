package io.github.hzhilong.bilibili.backup.api.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class LoginResult {
    private String url;
    @JSONField(name = "refresh_token")
    private String refreshToken;
    private Long timestamp;
    private Integer code;
    private String message;
}
