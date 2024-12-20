package io.github.hzhilong.bilibili.backup.gui.config;

import java.awt.*;

/**
 * 颜色配置
 *
 * @author hzhilong
 * @version 1.0
 */
public class ColorConfig {

    public static Color[] APP_COLORS = new Color[]{
            new Color(0, 174, 236),
            new Color(251, 114, 153),
            new Color(43, 43, 43),
            new Color(128, 122, 210),
            new Color(238, 187, 43),
            new Color(25, 190, 46),
            new Color(255, 154, 0),
            new Color(148, 20, 197),
    };

    public static Color get(int pos) {
        return APP_COLORS[pos % APP_COLORS.length];
    }
}
