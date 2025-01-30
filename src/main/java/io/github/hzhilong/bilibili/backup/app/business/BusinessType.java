package io.github.hzhilong.bilibili.backup.app.business;

import io.github.hzhilong.baseapp.business.IBusinessType;
import lombok.Getter;

/**
 * 业务类型
 *
 * @author hzhilong
 * @version 1.0
 */
@Getter
public enum BusinessType implements IBusinessType {

    BACKUP("备份"),
    RESTORE("还原"),
    CLEAR("清空");

    /**
     * 业务名
     */
    private final String name;

    BusinessType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
