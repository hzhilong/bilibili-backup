package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Bangumi
 * @Description 番剧
 * @Author hzhilong
 * @Time 2024/9/24
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class Bangumi {

    /**
     * season_id : 21076
     * media_id : 21076
     * season_type_name : 电视剧
     * title : 墓道
     * cover : http://i0.hdslb.com/bfs/bangumi/image/9605b1048e96cd7dea9a4da461d603535df9a644.jpg
     * total_count : 25
     * badge : 会员专享
     * badge_type : 0
     * url : https://www.bilibili.com/bangumi/play/ss21076
     * follow_status : 2
     */

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
