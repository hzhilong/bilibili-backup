package top.ybgnb.bilibili.backup.utils;

import java.util.List;

/**
 * @ClassName: ListUtil
 * @Author: 黄智龙
 * @Description：
 * @Date: 2019/5/14 5:40:39
 */
public class ListUtil {

    public static boolean isEmpty(List list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean notEmpty(List list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        return true;
    }

    public static int getSize(List list) {
        if (isEmpty(list)) {
            return 0;
        }
        return list.size();
    }

    public static String listToString(List list, char separator) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

}
