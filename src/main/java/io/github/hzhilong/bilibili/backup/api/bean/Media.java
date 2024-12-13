package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收藏的视频稿件
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class Media {
    private Long id;
    private Integer type;
    private String title;
    private String cover;
    private String intro;
    private Integer page;
    private Integer duration;
    private Upper upper;
    private Integer attr;
    private CntInfo cntInfo;
    private String link;
    private Integer ctime;
    private Integer pubtime;
    private Integer favTime;
    private String bvId;
    private String bvid;
}
