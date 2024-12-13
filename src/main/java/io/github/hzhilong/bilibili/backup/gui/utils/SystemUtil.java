package io.github.hzhilong.bilibili.backup.gui.utils;

import java.awt.*;
import java.util.LinkedHashSet;

/**
 * 系统工具
 *
 * @author hzhilong
 * @version 1.0
 */
public class SystemUtil {

    /**
     * 获取中文字体
     */
    public static LinkedHashSet<String> getCNFontNames() {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        String[] familyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String familyName : familyNames) {
            Font font = new Font(familyName, Font.PLAIN, 14);
            if (font.canDisplay('中')) {
                result.add(familyName);
            }
        }
        return result;
    }

}
