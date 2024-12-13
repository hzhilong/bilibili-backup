package io.github.hzhilong.bilibili.backup.api.bean.page;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.github.hzhilong.bilibili.backup.api.bean.FavInfo;

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
public class CursorPageData extends PageData<JSONObject> {
    /**
     * 是否有下一页
     */
    @JSONField(name="has_next")
    private boolean hasNext;

    private FavInfo info;

    private List<JSONObject> item;

    @Override
    public boolean hasMore(int currentTotal) {
        return this.hasNext;
    }

    @Override
    public List<JSONObject> getList() {
        return this.item;
    }

    @Override
    public void setList(List<JSONObject> list) {
        this.item = list;
    }
}
