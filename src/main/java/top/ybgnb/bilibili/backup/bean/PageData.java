package top.ybgnb.bilibili.backup.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PageData
 * @Description
 * @Author hzhilong
 * @Time 2024/9/20
 * @Version 1.0
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

    Boolean has_more;
    Integer count;

    /**
     * 是否有下一页
     *
     * @param currentTotal
     * @return
     */
    public boolean hasMore(int currentTotal) {
        if (this.has_more == null) {
            return this.getTotal() > currentTotal;
        } else {
            return this.has_more;
        }
    }

    public List<L> _getList() {
        return this.items != null ? this.items : this.list;
    }

    public void _setList(List<L> list) {
        if (this.items != null) {
            this.items = list;
        } else {
            this.list = list;
        }
    }
}
