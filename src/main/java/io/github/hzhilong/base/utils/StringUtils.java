package io.github.hzhilong.base.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具
 *
 * @author hzhilong
 * @version 1.0
 */
public class StringUtils {

    public static boolean notEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String stringToUnicode(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
        }
        return sb.toString();
    }

    public static String unicodeToString(String unicode) {
        if (unicode == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i;
        int pos = 0;

        while ((i = unicode.indexOf("\\u", pos)) != -1) {
            sb.append(unicode, pos, i);
            if (i + 5 < unicode.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }

        return sb.toString();
    }

    /**
     * 遍历字符串，以实际显示的字符转换成多个字符串数组
     */
    public static List<String> toStrings(String str) {
        // https://github.com/4rnold/Blog/issues/41
        // https://zh.wikipedia.org/wiki/Unicode%E4%B8%AD%E7%9A%84%E9%9F%B3%E6%A0%87%E7%AC%A6%E5%8F%B7
        // https://blog.csdn.net/wkk_ly/article/details/133888974
        // https://zh.wikipedia.org/wiki/Unicode%E5%AD%97%E7%AC%A6%E5%B9%B3%E9%9D%A2%E6%98%A0%E5%B0%84#%E5%9F%BA%E6%9C%AC%E5%A4%9A%E6%96%87%E7%A7%8D%E5%B9%B3%E9%9D%A2

        List<String> result = new ArrayList<>();
        for (int offset = 0; offset < str.length(); ) {
            //从offset开始获取字符，返回字符对应的unicode
            int currUnicode = str.codePointAt(offset);
            int nextOffset = Character.charCount(currUnicode);
            //根据unicode编码 转为char[]存储（utf16格式）。
            char[] utf16Chars = Character.toChars(currUnicode);

            if (nextOffset + offset < str.length()) {
                // 还有下一个字符
                int nextUnicode = str.codePointAt(nextOffset + offset);
                if (nextUnicode >= 0x0300 && nextUnicode <= 0x036f) {
                    // 组合附加符号
                    nextOffset++;
                    utf16Chars = new char[]{utf16Chars[0], Character.toChars(nextUnicode)[0]};
                }
            }

            //将chars转为String
            String s = String.valueOf(utf16Chars);
            result.add(s);
            //增加offset
            offset += nextOffset;
        }
        return result;
    }

}
