package io.github.hzhilong.bilibili.backup.gui.component.menu;

import io.github.hzhilong.bilibili.backup.app.state.AppData;

import javax.swing.*;
import java.awt.*;

/**
 * 【外观】菜单
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppearanceMenu extends JMenu {

    public AppearanceMenu() throws HeadlessException {
        super("外观");
        AppData appData = AppData.getInstance();
        add(new ThemeMenu(appData.getThemeDataItem()));
        add(new FontFamilyMenu(appData.getFontFamilyDataItem(), appData.getInitialFontFamily()));
        add(new FontSizeMenu(appData.getFontSizeDataItem(), appData.getInitialFontSize()));
    }
}
