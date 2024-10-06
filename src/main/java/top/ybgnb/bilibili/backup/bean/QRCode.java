package top.ybgnb.bilibili.backup.bean;

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
public class QRCode {
    private String url;
    private String qrcode_key;
}
