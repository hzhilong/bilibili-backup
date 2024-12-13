package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收藏夹元数据
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class FavInfo {
    private Long id;
    private Long fid;
    private Long mid;
    private Integer attr;
    private String title;
    private String cover;
    private Integer ctime;
    private Integer mediaCount;
}
