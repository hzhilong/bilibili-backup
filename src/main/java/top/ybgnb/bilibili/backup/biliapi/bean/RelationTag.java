package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RelationTag
 * @Description 关系分组
 * @Author hzhilong
 * @Time 2024/9/24
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class RelationTag {

    /**
     * tagid : -10
     * name : 特别关注
     * count : 1
     * tip : 第一时间收到该分组下用户更新稿件的通知
     */


    /**
     * tagid : 0
     * name : 默认分组
     * count : 1
     * tip :
     */

    private Long tagid;
    private String name;
    private Integer count;
    private String tip;

    public RelationTag(Long tagid, String name) {
        this.tagid = tagid;
        this.name = name;
    }
}
