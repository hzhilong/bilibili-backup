package top.ybgnb.bilibili.backup.biliapi.bean.page;

/**
 * @ClassName PageCallback
 * @Description 分页回调
 * @Author hzhilong
 * @Time 2024/12/6
 * @Version 1.0
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
