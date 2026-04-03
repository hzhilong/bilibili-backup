package io.github.hzhilong.bilibili.backup.api.bean.page;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 滑动 分页数据
 *
 * @author hzhilong
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class CursorPageData3 extends CursorPageTotal implements PageableData<JSONObject> {

    @Override
    public boolean hasMore(int currentTotal) {
        return this.getCursor() != null && !this.getCursor().isEnd();
    }

    @Override
    public List<JSONObject> getList() {
        return this.getItems();
    }

    @Override
    public void setList(List<JSONObject> list) {
        this.setItems(list);
    }
}
