package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Cursor
 * @Description 分页滑动信息
 * @Author hzhilong
 * @Time 2024/12/6
 * @Version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cursor {

    /**
     * max : 113326285655544
     * view_at : 1733474681
     * business : archive
     * ps : 20
     */

    private Long max;
    private Integer viewAt;
    private String business;
    private Integer ps;
}
