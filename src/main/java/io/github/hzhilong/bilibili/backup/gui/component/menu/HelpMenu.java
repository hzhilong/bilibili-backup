package io.github.hzhilong.bilibili.backup.gui.component.menu;

import io.github.hzhilong.bilibili.backup.gui.dialog.AboutDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutDialog(SwingUtilities.getWindowAncestor(HelpMenu.this)).setVisible(true);
            }
        });
        add(about);
    }
}
