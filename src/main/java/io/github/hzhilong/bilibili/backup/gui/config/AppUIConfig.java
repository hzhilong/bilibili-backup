package io.github.hzhilong.bilibili.backup.gui.config;

import com.formdev.flatlaf.extras.FlatSVGUtils;

import java.awt.*;

/**
 * 软件UI配置
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppUIConfig extends BaseConfig {

    public static void init() {
        // 字体
        FontConfig.init();
        // 主题
        ThemeConfig.init();
    }

    public static void initAppIcon(Window window) {
        window.setIconImages(FlatSVGUtils.createWindowIconImages("/icon/app_logo.svg"));
    }
}
