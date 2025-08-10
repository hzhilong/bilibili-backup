package io.github.hzhilong.bilibili.backup.app.utils;

/**
 * 分页工具
 *
 * @author hzhilong
 * @version 1.0
 */
public class PageUtils {

    /**
     * 获取页码范围内的数据量
     * @param total 总数
     * @param pageSize  分页大小
     * @param startPage 开始页码 1开始
     * @param endPage   结束页码
     * @return 数据量
     */
    public static int getDataCountInPageRange(int total, int pageSize, int startPage, int endPage) {
        if (pageSize <= 0 || total <= 0) return 0;

        int maxPage = (int) Math.ceil((double) total / pageSize);

        // 修正页码范围
        startPage = Math.max(1, startPage);
        endPage = Math.min(endPage, maxPage);
        if (startPage > endPage) return 0;

        int startIndex = (startPage - 1) * pageSize;
        int endIndex = Math.min(endPage * pageSize, total);

        return endIndex - startIndex;
    }
}
