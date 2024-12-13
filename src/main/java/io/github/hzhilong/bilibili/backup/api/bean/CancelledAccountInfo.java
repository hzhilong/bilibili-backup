package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 已注销账号信息
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelledAccountInfo {

    private String uid;
    private int followingCount;
    private int followerCount;

}
