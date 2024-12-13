package io.github.hzhilong.bilibili.backup.gui.config;

import com.formdev.flatlaf.FlatLaf;
import io.github.hzhilong.base.utils.StringUtils;

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
        String fontFamily = appData.getPreferredFontFamily();
        int fontSize = Integer.parseInt(appData.getPreferredFontSize());
        init(fontFamily, fontSize);
    }

    public static void init(String fontFamily, int fontSize) {
        Font font = new Font(fontFamily, Font.PLAIN, fontSize);
        UIManager.put("defaultFont", font);
    }

    public static void update(String fontFamily) {
        update(fontFamily, null);
    }

    public static void update(Integer fontSize) {
        update(null, fontSize);
    }

    public static void update(String fontFamily, Integer fontSize) {
        if (StringUtils.isEmpty(fontFamily)) {
            fontFamily = appData.getPreferredFontFamily();
        }
        if (fontSize == null) {
            fontSize = Integer.parseInt(appData.getPreferredFontSize());
        }
        Font font = new Font(fontFamily, Font.PLAIN, fontSize);
        UIManager.put("defaultFont", font);
        FlatLaf.updateUI();
    }


}
