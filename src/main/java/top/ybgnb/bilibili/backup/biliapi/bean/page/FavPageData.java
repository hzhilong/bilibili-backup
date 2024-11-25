package top.ybgnb.bilibili.backup.biliapi.bean.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.ybgnb.bilibili.backup.biliapi.bean.FavInfo;
import top.ybgnb.bilibili.backup.biliapi.bean.Media;

import java.util.List;

/**
 * @ClassName FavPageData
 * @Description 收藏夹明细
 * @Author hzhilong
 * @Time 2024/9/23
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class FavPageData extends PageData<Media>{
    /**
     * 收藏夹是否有下一页
     */
    private boolean has_more;

    private FavInfo info;

    private List<Media> medias;

    @Override
    public boolean hasMore(int currentTotal) {
        return this.has_more;
    }

    @Override
    public List<Media> _getList() {
        return this.medias;
    }

    @Override
    public void _setList(List<Media> list) {
       this.medias = list;
    }
}
