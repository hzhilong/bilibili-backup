package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多用户详细信息的结果
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCard2 {

    /**
     * mid : 3
     * name : 囧囧倉
     * face : http://i0.hdslb.com/bfs/face/d4de6a84557eea8f18510a3f61115d96832aa071.jpg
     * sign : 富强、民主、文明、和谐、自由、平等、公正、法治、爱国、敬业、诚信、友善。
     * rank : 10000
     * level : 6
     * silence : 0
     */

    private Long mid;
    private String name;
    private String face;
    private String sign;
    /**
     * 用户权限等级
     */
    private Integer rank;
    private Integer level;
    /**
     * 封禁状态 0：正常<br />1：被封
     */
    private Integer silence;
}
