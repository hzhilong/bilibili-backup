package io.github.hzhilong.bilibili.backup.gui.menu;

import io.github.hzhilong.bilibili.backup.App;
import io.github.hzhilong.bilibili.backup.gui.dialog.SettingDialog;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import javax.swing.*;
import java.awt.*;

/**
 * 【应用】菜单
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class AppMenu extends JMenu {

    public AppMenu() throws HeadlessException {
        super("应用");
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

        JMenuItem setting = new JMenuItem("设置");
        setting.addActionListener(e -> new SettingDialog(App.mainFrame).setVisible(true));
        add(setting);

        JMenuItem exitApp = new JMenuItem("退出");
        add(exitApp);
        exitApp.addActionListener(e -> App.exitApp());
    }
}
