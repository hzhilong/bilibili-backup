package top.ybgnb.bilibili.backup.constant;

/**
 * @ClassName BuType
 * @Description
 * @Author hzhilong
 * @Time 2024/9/29
 * @Version 1.0
 */
public enum BuType {
    BACKUP("backup", "备份"),
    RESTORE("restore", "还原"),
    READ_ALL_MSG("read_all_msg", "已读所有消息"),
    
    EXIT("exit", "退出程序");

    private final String name;

    private final String cnName;

    BuType(String name, String cnName) {
        this.name = name;
        this.cnName = cnName;
    }

    public String getName() {
        return name;
    }

    public String getCnName() {
        return cnName;
    }

    public static BuType parse(String name) {
        for (BuType buType : BuType.values()) {
            if (buType.name.equals(name)) {
                return buType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getFunctionCnName() {
        return this.cnName;
    }
}
