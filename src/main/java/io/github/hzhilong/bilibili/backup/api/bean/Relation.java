package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户关系
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class Relation {

    private Long mid;
    private Integer attribute;
    private Long mtime;
    private List<Long> tag;
    private Integer special;
    private String uname;
    private String face;
    private String sign;
}
