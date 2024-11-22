package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FavInfo
 * @Description 收藏夹元数据
 * @Author hzhilong
 * @Time 2024/9/23
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class FavInfo {

    /**
     * id : 3078391735
     * fid : 30783917
     * mid : 1221326335
     * attr : 1
     * title : 默认收藏夹
     * cover : http://i2.hdslb.com/bfs/archive/907ebbe82b62cde36bfd7cf8495e91a5eb8ddd48.jpg
     * ctime : 1712281820
     * media_count : 94
     */

    private Long id;
    private Long fid;
    private Long mid;
    private Integer attr;
    private String title;
    private String cover;
    private Integer ctime;
    private Integer mediaCount;
}
