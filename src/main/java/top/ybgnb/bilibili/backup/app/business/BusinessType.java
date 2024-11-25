package top.ybgnb.bilibili.backup.app.business;

import top.ybgnb.bilibili.backup.app.business.impl.BackupBusiness;
import top.ybgnb.bilibili.backup.app.business.impl.CancelledAccountsBusiness;
import top.ybgnb.bilibili.backup.app.business.impl.ReadAllMessageBusiness;
import top.ybgnb.bilibili.backup.app.business.impl.RestoreBusiness;
import top.ybgnb.bilibili.backup.app.business.impl.UserManageBusiness;

/**
 * @ClassName BuType
 * @Description 业务类型
 * @Author hzhilong
 * @Time 2024/9/29
 * @Version 1.0
 */
public enum BusinessType {
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

    BusinessType(String name, Class<? extends BaseBusiness> businessClass) {
        this.name = name;
        this.businessClass = businessClass;
    }

    public String getName() {
        return name;
    }

    public Class<? extends BaseBusiness> getBusinessClass() {
        return businessClass;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
