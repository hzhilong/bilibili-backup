package io.github.hzhilong.bilibili.backup.gui.dialog;

import io.github.hzhilong.bilibili.backup.gui.page.AboutPage;

import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 关于
 *
 * @author hzhilong
 * @version 1.0
 */
public class AboutDialog extends BaseDialog {

    public AboutDialog(Window parent) {
        super(parent, "关于");
        initUI();
        setModal(true);
    }

    private void initUI() {
        // 点击对话框关闭按钮时，销毁对话框
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        AboutPage contentPane = new AboutPage(null);
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        setResizable(false);
    }

}
