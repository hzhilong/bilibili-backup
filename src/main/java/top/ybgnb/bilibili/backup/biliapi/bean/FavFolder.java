package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FavFolder
 * @Description 收藏夹
 * @Author hzhilong
 * @Time 2024/9/22
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class FavFolder {

    /**
     * id : 3078391735
     * fid : 30783917
     * mid : 1221326335
     * attr : 1
     * title : 默认收藏夹
     * fav_state : 0
     * media_count : 94
     */

    /**
     * id	num	收藏夹mlid（完整id）	收藏夹原始id+创建者mid尾号2位
     * fid	num	收藏夹原始id
     * mid	num	创建者mid
     * attr	num	属性位
     * title	str	收藏夹标题
     * fav_state	num	目标id是否存在于该收藏夹	存在于该收藏夹：1 不存在于该收藏夹：0
     * media_count	num	收藏夹内容数量
     *
     * attr属性位二进制值表：
     *
     * 位	内容	            备注
     * 1	私有收藏夹	        0：公开 1：私有
     * 2	是否为默认收藏夹	0：默认收藏夹 1：其他收藏夹
     *
     * 默认收藏夹 公开 0       0 0000
     * 默认收藏夹 私有 1       0 0001
     * 其他收藏夹 公开 22      1 0110
     * 其他收藏夹 私有 23      1 0111
     * 其他收藏夹 公开 54     11 0110
     * 其他收藏夹 私有 55     11 0111
     */

    private Long id;
    private Long fid;
    private Long mid;
    private Integer attr;
    private String title;
    private String intro;
    private String cover;
    private Integer favState;
    private Integer mediaCount;
}
