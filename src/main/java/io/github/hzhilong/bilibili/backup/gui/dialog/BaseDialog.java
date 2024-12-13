package io.github.hzhilong.bilibili.backup.gui.dialog;

import io.github.hzhilong.bilibili.backup.gui.config.AppUIConfig;

import javax.swing.*;
import java.awt.*;

/**
 * 软件对话框基类
 *
 * @author hzhilong
 * @version 1.0
 */
public class BaseDialog extends JDialog {

    private final Window parent;
    private final String title;

    public BaseDialog(Window parent, String title) {
        super(parent);
        this.parent = parent;
        this.title = title;

        initUI();
    }

    private void initUI() {
        setTitle(title);
        AppUIConfig.initAppIcon(this);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            pack();
            setLocationRelativeTo(parent);
        }
        super.setVisible(visible);
    }
}
