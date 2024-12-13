package io.github.hzhilong.bilibili.backup.gui.config;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import io.github.hzhilong.base.utils.StringUtils;

import javax.swing.*;

/**
 * 主题配置
 *
 * @author hzhilong
 * @version 1.0
 */
public class ThemeConfig extends BaseConfig {

    private static final Class<? extends FlatLaf>[] DEFAULT_THEMES = new Class[]{
            FlatLightLaf.class,
            FlatDarkLaf.class,
            FlatIntelliJLaf.class,
            FlatDarculaLaf.class,
            FlatMacLightLaf.class,
            FlatMacDarkLaf.class,
            FlatSolarizedLightIJTheme.class,
    };

    private static final Class<? extends FlatLaf> DEFAULT_THEME = FlatDarculaLaf.class;

    public static Class<? extends FlatLaf> getDefaultTheme() {
        return DEFAULT_THEME;
    }

    public static Class<? extends FlatLaf>[] getDefaultThemes() {
        return DEFAULT_THEMES;
    }

    public static void init() {
        init(appData.getThemeDataItem().getValue());
    }

    public static void init(String themeClassName) {
        // 启用 FlatLaf 主题库
        try {
            if (StringUtils.isEmpty(themeClassName)) {
                themeClassName = DEFAULT_THEME.getName();
            }
            UIManager.setLookAndFeel(themeClassName);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        // 启用 FlatLaf 窗口装饰
        System.setProperty("flatlaf.useWindowDecorations", "true");
    }

    public static void update(String currThemeClassName) {
        if (currThemeClassName.equals(UIManager.getLookAndFeel().getClass().getName())) {
            return;
        }

        FlatAnimatedLafChange.showSnapshot();
        try {
            UIManager.setLookAndFeel(currThemeClassName);
        } catch (Exception ex) {
            throw new RuntimeException("切换主题失败：" + ex.getMessage());
        }
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

}
