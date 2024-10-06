package top.ybgnb.bilibili.backup.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FavCollected
 * @Description 收藏的视频合集
 * @Author hzhilong
 * @Time 2024/9/23
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class FavCollected {

    /**
     * id : 2451182
     * fid : 0
     * mid : 673835935
     * title : 程序的奥秘
     * cover : https://s1.hdslb.com/bfs/templar/york-static/viedeo_material_default.png
     * upper : {"mid":673835935,"name":"码农的荒岛求生","face":""}
     * ctime : 0
     * media_count : 12
     */

    private Long id;
    private Long fid;
    private Long mid;
    private String title;
    private String cover;
    private Upper upper;
    private Integer ctime;
    private Integer mediaCount;

}
