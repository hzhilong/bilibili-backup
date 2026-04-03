package io.github.hzhilong.bilibili.backup.api.bean.page;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 滑动 分页数据
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class CursorPageData2 implements PageableData<JSONObject> {
    /**
     * 是否有下一页
     */
    @JSONField(name = "has_next")
    private boolean hasNext;

    private CursorPageTotal total;

    @Override
    public boolean hasMore(int currentTotal) {
        return this.total.getCursor() != null && !this.total.getCursor().isEnd();
    }

    @Override
    public List<JSONObject> getList() {
        return this.total.getItems();
    }

    @Override
    public void setList(List<JSONObject> list) {
        this.total.setItems(list);
    }
}
