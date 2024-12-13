package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频稿件
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class Video {
    private Long aid;
    private String bvid;
    private String pic;
    private String title;
    private Integer pubdate;
    private Integer duration;
    private Upper owner;
}
