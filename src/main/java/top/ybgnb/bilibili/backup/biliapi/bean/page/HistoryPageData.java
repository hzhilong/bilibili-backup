package top.ybgnb.bilibili.backup.biliapi.bean.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.ybgnb.bilibili.backup.biliapi.bean.Cursor;
import top.ybgnb.bilibili.backup.biliapi.bean.History;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;

import java.util.List;

/**
 * @ClassName HistoryPageData
 * @Description 历史记录
 * @Author hzhilong
 * @Time 2024/12/06
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class HistoryPageData extends PageData<History> {

    private Cursor cursor;

    private List<History> list;

    @Override
    public boolean hasMore(int currentTotal) {
        if (cursor == null || cursor.getMax() == null || cursor.getMax() == 0 || ListUtil.isEmpty(list)) {
            return false;
        }
        return true;
    }

    @Override
    public List<History> _getList() {
        return this.list;
    }

    @Override
    public void _setList(List<History> list) {
        this.list = list;
    }
}
