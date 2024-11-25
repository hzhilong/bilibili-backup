package top.ybgnb.bilibili.backup.biliapi.bean.page;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.ybgnb.bilibili.backup.biliapi.bean.FavInfo;

import java.util.List;

/**
 * @ClassName CursorPageData
 * @Description 翻页数据
 * @Author hzhilong
 * @Time 2024/11/25
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class CursorPageData extends PageData<JSONObject> {
    /**
     * 是否有下一页
     */
    private boolean has_next;

    private FavInfo info;

    private List<JSONObject> item;

    @Override
    public boolean hasMore(int currentTotal) {
        return this.has_next;
    }

    @Override
    public List<JSONObject> _getList() {
        return this.item;
    }

    @Override
    public void _setList(List<JSONObject> list) {
        this.item = list;
    }
}
