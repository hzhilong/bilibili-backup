package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;

/**
 * @ClassName Upper
 * @Description
 * @Author hzhilong
 * @Time 2024/9/23
 * @Version 1.0
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Upper {
    private Long mid;
    private String name;
    private String face;

    /**
     * 是否已注销
     */
    public boolean _isCancelledAccount() {
        return AppConstant.CANCELLED_ACCOUNT_NAME.equals(name);
    }
}
