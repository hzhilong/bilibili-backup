package io.github.hzhilong.bilibili.backup.api.bean.page;

/**
 * 分页回调
 *
 * @author hzhilong
 * @version 1.0
 */
public interface PageCallback {
    /**
     * 分页回调
     *
     * @param page         当前页码
     * @param currPageSize 当前页页码数量
     * @param totalSize    总数据量
     */
    void page(int page, int currPageSize, int totalSize);
}
