package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName Relation
 * @Description 用户关系
 * @Author hzhilong
 * @Time 2024/9/30
 * @Version 1.0
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
