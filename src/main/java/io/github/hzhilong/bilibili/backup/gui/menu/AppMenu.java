package io.github.hzhilong.bilibili.backup.gui.menu;

import io.github.hzhilong.baseapp.component.CheckBoxSettingItem;
import io.github.hzhilong.baseapp.component.InputSettingItem;
import io.github.hzhilong.baseapp.dialog.BaseSettingDialog;
import io.github.hzhilong.baseapp.menu.BaseMenu;
import io.github.hzhilong.bilibili.backup.app.state.setting.AppSettingItems;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * 【应用】菜单
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class AppMenu extends BaseMenu {

    public AppMenu(Window parent, String appIconPath) {
        super("应用", parent, appIconPath);
        JCheckBoxMenuItem showLogs = new JCheckBoxMenuItem("显示日志");
        add(showLogs);
        showLogs.addActionListener(e -> {
            if (Level.INFO.equals(LogManager.getRootLogger().getLevel())) {
                LogManager.getRootLogger().setLevel(Level.DEBUG);
                showLogs.setSelected(true);
            } else {
                LogManager.getRootLogger().setLevel(Level.INFO);
                showLogs.setSelected(false);
            }
        });

        JMenuItem setting = getSettingMenu();
        add(setting);

        JMenuItem exitApp = new JMenuItem("退出");
        add(exitApp);
        exitApp.addActionListener(e -> parent.dispose());
    }

    private JMenuItem getSettingMenu() {
        JMenuItem setting = new JMenuItem("设置");
        setting.addActionListener(e ->
                new BaseSettingDialog(parentWindow, appIconPath,
                        new ArrayList() {{
                            add(new CheckBoxSettingItem(AppSettingItems.DIRECT_RESTORE));
                            add(new CheckBoxSettingItem(AppSettingItems.ALLOW_FAILURE));
                            add(new CheckBoxSettingItem(AppSettingItems.FAV_SAVE_TO_DEFAULT_ON_FAILURE));
                            add(new CheckBoxSettingItem(AppSettingItems.SELECT_FAV));
                            add(new CheckBoxSettingItem(AppSettingItems.SELECT_RELATION_TAG));
                            add(new InputSettingItem(AppSettingItems.OPENAI_API_URL));
                            add(new InputSettingItem(AppSettingItems.OPENAI_API_KEY));
                            add(new InputSettingItem(AppSettingItems.OPENAI_API_MODEL));
                            add(new CheckBoxSettingItem(AppSettingItems.AUTO_SUBMIT_ANSWER));
                        }}).setVisible(true));
        return setting;
    }
}
