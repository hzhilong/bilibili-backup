package top.ybgnb.bilibili.backup.biliapi.bean;

/**
 * @ClassName RelationAct
 * @Description 关系操作代码
 * @Author hzhilong
 * @Time 2024/9/30
 * @Version 1.0
 */
public enum RelationAct {
    FOLLOW(1, "关注"),
    UNFOLLOW(2, "取关"),
    BLOCK(5, "拉黑"),
    UNBLOCK(6, "取消拉黑"),
    REMOVE_FOLLOWER(7, "踢出粉丝");
    private int code;
    private String name;

    RelationAct(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
