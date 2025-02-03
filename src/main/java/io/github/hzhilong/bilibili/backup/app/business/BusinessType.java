package io.github.hzhilong.bilibili.backup.app.business;

import io.github.hzhilong.baseapp.business.IBusinessType;

/**
 * 业务类型
 *
 * @author hzhilong
 * @version 1.0
 */
public enum BusinessType implements IBusinessType {

    BACKUP("备份"),
    RESTORE("还原"),
    CLEAR("清空");

    /**
     * 业务名
     */
    private String name;

    BusinessType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
