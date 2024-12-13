package io.github.hzhilong.bilibili.backup.api.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二维码信息
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class QRCode {
    private String url;

    @JSONField(name = "qrcode_key")
    private String qrcodeKey;
}
