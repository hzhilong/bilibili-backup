package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专栏
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class Opus {

    private String jumpUrl;
    private Long opusId;
    private String timeText;
    private String title;
}
