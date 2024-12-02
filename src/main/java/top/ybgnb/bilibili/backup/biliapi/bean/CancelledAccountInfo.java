package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CancelledAccountInfo
 * @Description 已注销账号信息
 * @Author hzhilong
 * @Time 2024/11/30
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelledAccountInfo {

    private String uid;
    private int followingCount;
    private int followerCount;

}
