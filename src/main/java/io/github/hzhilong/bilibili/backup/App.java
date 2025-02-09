package io.github.hzhilong.bilibili.backup;

import io.github.hzhilong.baseapp.BaseApp;
import io.github.hzhilong.bilibili.backup.gui.frame.MainFrame;

/**
 * GUI程序
 *
 * @author hzhilong
 * @version 1.0
 */
public class App extends BaseApp {

    public static void main(String[] args) {
        new App().init().open(new MainFrame());
    }

}
