package io.github.hzhilong.bilibili.backup;

import io.github.hzhilong.bilibili.backup.gui.config.AppUIConfig;
import io.github.hzhilong.bilibili.backup.gui.page.MainFrame;

import javax.swing.*;

/**
 * GUI程序
 *
 * @author hzhilong
 * @version 1.0
 */
public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().openApp());
    }

    public void openApp() {
        AppUIConfig.init();
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
