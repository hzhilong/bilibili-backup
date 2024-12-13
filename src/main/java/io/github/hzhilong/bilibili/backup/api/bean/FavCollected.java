package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收藏的视频合集
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class FavCollected {

    private Long id;
    private Long fid;
    private Long mid;
    private String title;
    private String cover;
    private Upper upper;
    private Integer ctime;
    private Integer mediaCount;

}
