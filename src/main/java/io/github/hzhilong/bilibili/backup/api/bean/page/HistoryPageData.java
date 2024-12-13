package io.github.hzhilong.bilibili.backup.api.bean.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.github.hzhilong.bilibili.backup.api.bean.Cursor;
import io.github.hzhilong.bilibili.backup.api.bean.History;
import io.github.hzhilong.base.utils.ListUtil;

import java.util.List;

/**
 * 历史记录 分页数据
 *
 * @author hzhilong
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class HistoryPageData extends PageData<History> {

    private Cursor cursor;

    @Override
    public boolean hasMore(int currentTotal) {
        return cursor != null && cursor.getMax() != null && cursor.getMax() != 0 && !ListUtil.isEmpty(list);
    }

    @Override
    public List<History> getList() {
        return this.list;
    }

    @Override
    public void setList(List<History> list) {
        this.list = list;
    }
}
