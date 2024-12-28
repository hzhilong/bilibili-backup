package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Getter;

/**
 * 关系操作代码
 *
 * @author hzhilong
 * @version 1.0
 */
@Getter
public enum RelationAct {
    FOLLOW(1, "关注", "11"),
    UNFOLLOW(2, "取关", "11"),
    BLOCK(5, "拉黑", "11"),
    UNBLOCK(6, "取消拉黑", "116"),
    REMOVE_FOLLOWER(7, "踢出粉丝", "11");
    private final int code;
    private final String name;
    private final String reSrc;

    RelationAct(int code, String name, String reSrc) {
        this.code = code;
        this.name = name;
        this.reSrc = reSrc;
    }


}
