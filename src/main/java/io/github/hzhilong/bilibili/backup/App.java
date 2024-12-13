package io.github.hzhilong.bilibili.backup;

import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.app.state.appdata.AppData;
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

    public static MainFrame mainFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().openApp());
    }

    public void openApp() {
        AppUIConfig.init();
        AppData.initAllData();
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    public static void exitApp() {
        if (GlobalState.getProcessing()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "正在操作，请勿退出！",
                    "提示", JOptionPane.WARNING_MESSAGE);
        } else {
            AppData.getInstance().setWindowsXY(mainFrame.getLocation().x, mainFrame.getLocation().y);
            AppData.getInstance().setWindowsSize(mainFrame.getSize().width, mainFrame.getSize().height);
            mainFrame.dispose();
            System.exit(0);
        }
    }

}
