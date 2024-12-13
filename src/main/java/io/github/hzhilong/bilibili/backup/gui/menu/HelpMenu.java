package io.github.hzhilong.bilibili.backup.gui.menu;

import io.github.hzhilong.bilibili.backup.App;
import io.github.hzhilong.bilibili.backup.gui.dialog.AboutDialog;

import javax.swing.*;
import java.awt.*;

/**
 * 【帮助】菜单
 *
 * @author hzhilong
 * @version 1.0
 */
public class HelpMenu extends JMenu {

    public HelpMenu() throws HeadlessException {
        super("帮助");

        JMenuItem about = new JMenuItem("关于");
        about.addActionListener(e -> new AboutDialog(App.mainFrame).setVisible(true));
        add(about);
    }
}
