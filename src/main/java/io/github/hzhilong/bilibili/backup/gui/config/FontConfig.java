package io.github.hzhilong.bilibili.backup.gui.config;

import com.formdev.flatlaf.FlatLaf;
import io.github.hzhilong.bilibili.backup.app.state.appdata.AppDataItem;

import javax.swing.*;
import java.awt.*;

/**
 * 软件字体配置
 *
 * @author hzhilong
 * @version 1.0
 */
public class FontConfig extends BaseConfig {

    public static void init() {
        String fontFamily = AppDataItem.FONT_FAMILY.getValue();
        Integer fontSize = AppDataItem.FONT_SIZE.getValue();
        if (fontFamily != null || fontSize != null) {
            init(fontFamily, fontSize);
        }
    }

    public static String getPreferredFontFamily(String value) {
        return getPreferredValue(value, AppDataItem.FONT_FAMILY, AppDataItem.INITIAL_FONT_FAMILY);
    }

    public static Integer getPreferredFontSize(Integer value) {
        return getPreferredValue(value, AppDataItem.FONT_SIZE, AppDataItem.INITIAL_FONT_SIZE);
    }

    private static Font getPreferredFont(String fontFamily, Integer fontSize) {
        return new Font(getPreferredFontFamily(fontFamily), Font.PLAIN, getPreferredFontSize(fontSize));
    }

    public static void init(String fontFamily, Integer fontSize) {
        UIManager.put("defaultFont", getPreferredFont(fontFamily, fontSize));
    }

    public static void update(String fontFamily) {
        update(fontFamily, null);
    }

    public static void update(Integer fontSize) {
        update(null, fontSize);
    }

    public static void update(String fontFamily, Integer fontSize) {
        UIManager.put("defaultFont", getPreferredFont(fontFamily, fontSize));
        FlatLaf.updateUI();
    }


}
