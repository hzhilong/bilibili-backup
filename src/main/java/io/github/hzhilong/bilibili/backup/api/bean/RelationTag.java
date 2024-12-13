package io.github.hzhilong.bilibili.backup.api.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关系分组
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class RelationTag {

    @JSONField(name = "tagid")
    private Long tagId;
    private String name;
    private Integer count;
    private String tip;

    public RelationTag(Long tagId, String name) {
        this.tagId = tagId;
        this.name = name;
    }
}
