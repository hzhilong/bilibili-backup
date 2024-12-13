package io.github.hzhilong.bilibili.backup.api.bean.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.github.hzhilong.bilibili.backup.api.bean.FavInfo;
import io.github.hzhilong.bilibili.backup.api.bean.Media;

import java.util.List;

/**
 * 收藏夹明细 分页数据
 *
 * @author hzhilong
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class FavPageData extends PageData<Media> {

    private FavInfo info;

    private List<Media> medias;

    @Override
    public boolean hasMore(int currentTotal) {
        return getHasMore();
    }

    @Override
    public List<Media> getList() {
        return this.medias;
    }

    @Override
    public void setList(List<Media> list) {
        this.medias = list;
    }
}
