package io.github.hzhilong.bilibili.backup.api.bean.page;

import java.util.List;

/**
 * 分页数据
 *
 * @author hzhilong
 * @version 1.0
 */
public interface PageableData<L> {

    boolean hasMore(int currentTotal);

    List<L> getList();

    void setList(List<L> list);
}
