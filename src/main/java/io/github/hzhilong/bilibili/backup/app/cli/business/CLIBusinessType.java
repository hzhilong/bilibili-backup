package io.github.hzhilong.bilibili.backup.app.cli.business;

import io.github.hzhilong.bilibili.backup.app.business.IBusinessType;
import io.github.hzhilong.bilibili.backup.app.cli.business.impl.BackupBusiness;
import io.github.hzhilong.bilibili.backup.app.cli.business.impl.CancelledAccountsBusiness;
import io.github.hzhilong.bilibili.backup.app.cli.business.impl.ReadAllMessageBusiness;
import io.github.hzhilong.bilibili.backup.app.cli.business.impl.RestoreBusiness;
import io.github.hzhilong.bilibili.backup.app.cli.business.impl.UserManageBusiness;
import lombok.Getter;

/**
 * 命令行扩展的业务类型
 *
 * @author hzhilong
 * @version 1.0
 */
@Getter
public enum CLIBusinessType implements IBusinessType {
    BACKUP("备份", BackupBusiness.class),
    RESTORE("还原", RestoreBusiness.class),
    READ_ALL_MSG("已读所有消息", ReadAllMessageBusiness.class),
    CANCELLED_ACCOUNTS("已注销账号数据", CancelledAccountsBusiness.class),
    USER_MANAGE("管理已登录账号", UserManageBusiness.class),
    EXIT("退出程序", null);

    /**
     * 业务名
     */
    private final String name;

    /**
     * 业务执行类
     */
    private final Class<? extends BaseBusiness> businessClass;

    CLIBusinessType(String name, Class<? extends BaseBusiness> businessClass) {
        this.name = name;
        this.businessClass = businessClass;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
