package io.github.hzhilong.base.utils;

import java.util.List;

/**
 * 列表工具
 *
 * @author hzhilong
 * @version 1.0
 */
public class ListUtil {

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean notEmpty(List<?> list) {
        return !isEmpty(list);
    }

    public static int getSize(List<?> list) {
        if (isEmpty(list)) {
            return 0;
        }
        return list.size();
    }

    public static String listToString(List<?> list, String separator) {
        if (isEmpty(list)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : list) {
            sb.append(o).append(separator);
        }
        return sb.substring(0, sb.toString().length() - separator.length());
    }

}
