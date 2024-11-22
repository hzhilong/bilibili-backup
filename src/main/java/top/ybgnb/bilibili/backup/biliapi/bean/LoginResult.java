package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QRCode
 * @Description
 * @Author hzhilong
 * @Time 2024/9/28
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class LoginResult {

    /**
     * url	str	游戏分站跨域登录 url	未登录为空
     * refresh_token	str	刷新refresh_token	未登录为空
     * timestamp	num	登录时间	未登录为0
     * 时间戳 单位为毫秒
     * code	num	0：扫码登录成功
     * 86038：二维码已失效
     * 86090：二维码已扫码未确认
     * 86101：未扫码
     * message	str	扫码状态信息
     */

    private String url;
    private String refresh_token;
    private Long timestamp;
    private Integer code;
    private String message;

}
