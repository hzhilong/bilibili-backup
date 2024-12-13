package io.github.hzhilong.bilibili.backup.gui.menu;

import io.github.hzhilong.bilibili.backup.app.state.appdata.AppData;
import io.github.hzhilong.bilibili.backup.app.state.appdata.AppDataItem;

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
        add(new ThemeMenu(AppDataItem.THEME));
        add(new FontFamilyMenu(AppDataItem.FONT_FAMILY, AppDataItem.INITIAL_FONT_FAMILY.getValue()));
        add(new FontSizeMenu(AppDataItem.FONT_SIZE, AppDataItem.INITIAL_FONT_SIZE.getValue()));
    }
}
