package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 番剧
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class Bangumi {
    private Long seasonId;
    private Long mediaId;
    private String seasonTypeName;
    private String title;
    private String cover;
    private Integer totalCount;
    private String badge;
    private Integer badgeType;
    private String url;
    private Integer followStatus;
}
