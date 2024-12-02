package top.ybgnb.bilibili.backup;

import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.ui.config.FlatLafConfig;
import top.ybgnb.bilibili.backup.ui.page.MainFrame;

import javax.swing.*;

/**
 * @ClassName App
 * @Description app
 * @Author hzhilong
 * @Time 2024/11/26
 * @Version 1.0
 */
public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new App().openApp();
            }
        });
    }

    public void openApp() {
        // FlatLaf 外观库
        FlatLafConfig.init();
        MainFrame frame = new MainFrame();
        try {
            frame.init();
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
    }
}
