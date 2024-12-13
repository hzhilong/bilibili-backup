package io.github.hzhilong.bilibili.backup.api.bean.page;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.hzhilong.base.utils.ListUtil;

import java.util.List;

/**
 * 分页数据
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class PageData<L> {

    List<L> list;

    List<L> items;

    Integer total;

    // 第几页
    Integer pn;

    // 每页数量
    Integer ps;

    @JSONField(name="has_more")
    Boolean hasMore;

    Integer count;

    /**
     * 是否有下一页
     */
    public boolean hasMore(int currentTotal) {
        if (this.hasMore == null) {
            return ListUtil.notEmpty(getList()) && this.getTotal() > currentTotal;
        } else {
            return this.hasMore;
        }
    }

    public List<L> getList() {
        return this.items != null ? this.items : this.list;
    }

    public void setList(List<L> list) {
        if (this.items != null) {
            this.items = list;
        } else {
            this.list = list;
        }
    }
}
