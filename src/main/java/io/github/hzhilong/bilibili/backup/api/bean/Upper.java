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
    private String sex;
    private Integer level;
    private Boolean noFace;

    public Upper(Long mid, String name, String face) {
        this.mid = mid;
        this.name = name;
        this.face = face;
    }

    /**
     * 是否已注销
     */
    public boolean isCancelledAccount() {
        return AppConstant.CANCELLED_ACCOUNT_NAME.equals(name);
    }
}
