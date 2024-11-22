package top.ybgnb.bilibili.backup.biliapi.utils;

import java.util.List;

/**
 * @ClassName: ListUtil
 * @Author: 黄智龙
 * @Description：
 * @Date: 2019/5/14 5:40:39
 */
public class ListUtil {

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean notEmpty(List list) {
        return !isEmpty(list);
    }

    public static int getSize(List list) {
        if (isEmpty(list)) {
            return 0;
        }
        return list.size();
    }

    public static String listToString(List list, String separator) {
        if (isEmpty(list)) return "";
        StringBuilder sb = new StringBuilder();
        for (Object o : list) {
            sb.append(o).append(separator);
        }
        return sb.substring(0, sb.toString().length() - separator.length());
    }

}
