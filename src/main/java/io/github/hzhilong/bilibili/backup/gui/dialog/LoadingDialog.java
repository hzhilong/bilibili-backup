package io.github.hzhilong.bilibili.backup.gui.dialog;

import io.github.hzhilong.bilibili.backup.gui.utils.LayoutUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @ClassName LoadingDialog
 * @Description 加载框
 * @Author hzhilong
 * @Time 2024/11/30
 * @Version 1.0
 */
public class LoadingDialog extends JDialog {

    public LoadingDialog(String msg) {
        super();
        setTitle("提示");
        setLocationRelativeTo(null);
        setSize(300, 180);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        LayoutUtil.addGridBarY(contentPane, new JLabel(msg), 0);
        LayoutUtil.addGridBarY(contentPane, progressBar, 1);
    }

    public void showDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoadingDialog.this.setVisible(true);
            }
        });
    }

    public void closeDialog(int minTime) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(minTime);
                return null;
            }

            @Override
            protected void done() {
                LoadingDialog.this.setVisible(false);
            }
        }.execute();
    }

}
