package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 滑动信息
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cursor {

    private Long max;
    private Integer viewAt;
    private String business;
    private Integer ps;
}
