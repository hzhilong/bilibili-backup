package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;

/**
 * UP主
 *
 * @author hzhilong
 * @version 1.0
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
    public boolean isCancelledAccount() {
        return AppConstant.CANCELLED_ACCOUNT_NAME.equals(name);
    }
}
