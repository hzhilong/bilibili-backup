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
    /**
     * attr属性位二进制值表：
     * 位	内容	            备注
     * 1	私有收藏夹	        0：公开 1：私有
     * 2	是否为默认收藏夹	0：默认收藏夹 1：其他收藏夹
     * 默认收藏夹 公开 0       0 0000
     * 默认收藏夹 私有 1       0 0001
     * 其他收藏夹 公开 22      1 0110
     * 其他收藏夹 私有 23      1 0111
     * 其他收藏夹 公开 54     11 0110
     * 其他收藏夹 私有 55     11 0111
     */
    private Integer attr;
    private String title;
    private String cover;
    private Integer ctime;
    private Integer mediaCount;

    public boolean isDefault() {
        if (attr == null) {
            return false;
        }
        return (attr >> 1 & 1) != 1;
    }

    public int getRemainingCount() {
        return this.isDefault() ? (50000 - this.getMediaCount()) : 1000 - this.getMediaCount();
    }
}
