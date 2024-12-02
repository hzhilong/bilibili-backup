package top.ybgnb.bilibili.backup.ui.page;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.ui.component.BackupRestoreItemSelector;
import top.ybgnb.bilibili.backup.ui.component.PagePanel;
import top.ybgnb.bilibili.backup.ui.component.UserSelector;
import top.ybgnb.bilibili.backup.ui.state.GlobalState;
import top.ybgnb.bilibili.backup.ui.worker.BackupRestoreRunnable;
import top.ybgnb.bilibili.backup.ui.worker.BackupRunnable;
import top.ybgnb.bilibili.backup.ui.worker.BuCallback;
import top.ybgnb.bilibili.backup.ui.worker.DelaySetProcessingLoggerRunnable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;

/**
 * @ClassName BackupPage
 * @Description 备份页面
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public class BackupPage extends PagePanel {

    public static final String BU_NAME = "备份";
    public static final String ACTIVE_BTN_NAME = "开始" + BU_NAME;
    public static final String INACTIVE_BTN_NAME = "取消" + BU_NAME;

    private UserSelector userSelector;

    private BackupRestoreItemSelector backupRestoreItemSelector;

    private JButton btnBackup;

    private JTextArea txtLog;

    private JScrollPane scrollPaneLog;

    private BackupRestoreRunnable backupRunnable;

    public BackupPage(OkHttpClient client) {
        super(client);
    }

    @Override
    public void initData() throws BusinessException {

    }

    @Override
    public void initUI() throws BusinessException {
        super.initUI();
        int posY = 0;

        userSelector = new UserSelector(client);
        addFixedContent(userSelector, 0, posY++);
        addSeparatorToFixed(0, posY++);

        posY = 0;
        backupRestoreItemSelector = new BackupRestoreItemSelector(null, 3);
        addDynamicContent(backupRestoreItemSelector, 0, posY++);

        btnBackup = new JButton(ACTIVE_BTN_NAME);
        addDynamicContent(btnBackup, 0, posY++);

        scrollPaneLog = addTxtLogToDynamic(0, posY++);
        txtLog = (JTextArea) scrollPaneLog.getViewport().getView();

        setDynamicContentVisible(false);
        initListener();
    }

    private void initListener() {
        userSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDynamicContentVisible(userSelector.getCurrUser() != null);
            }
        });
        btnBackup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBtnBackup();
            }
        });
    }

    private void onBtnBackup() {
        if (ACTIVE_BTN_NAME.equals(this.btnBackup.getText())) {
            if (GlobalState.getProcessing()) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "有其他任务在运行！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LinkedHashSet<ServiceBuilder> items = backupRestoreItemSelector.getSelectedItems();
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请至少选择一项！", "提示", JOptionPane.OK_OPTION);
                return;
            }
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "是否开始" + BU_NAME + "？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                backup();
            }
        } else {
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "正在进行" + BU_NAME + "，是否取消？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                stopBackup();
            }
        }
    }

    private void setBusyStatus(boolean flag) {
        if (flag) {
            this.btnBackup.setText(INACTIVE_BTN_NAME);
        } else {
            this.btnBackup.setText(ACTIVE_BTN_NAME);
        }
        userSelector.setEnabled(!flag);
        backupRestoreItemSelector.setEnabled(!flag);
        if (flag) {
            GlobalState.setProcessingLogger(txtLog);
        } else {
            new Thread(new DelaySetProcessingLoggerRunnable(null)).start();
        }
        GlobalState.setProcessing(flag);
    }

    private void backup() {
        setBusyStatus(true);
        backupRunnable = new BackupRunnable(client, userSelector.getCurrUser(),
                backupRestoreItemSelector.getSelectedItems(),
                new BuCallback<Void>() {
                    @Override
                    public void success(Void data) {
                        setBusyStatus(false);
                    }

                    @Override
                    public void fail(String msg) {
                        setBusyStatus(false);
                    }

                    @Override
                    public void interrupt() {
                        setBusyStatus(false);
                    }
                });
        new Thread(backupRunnable).start();
    }

    private void stopBackup() {
        log.info("中断任务中...");
        backupRunnable.setInterrupt(true);
    }
}