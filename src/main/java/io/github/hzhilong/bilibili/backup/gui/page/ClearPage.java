package io.github.hzhilong.bilibili.backup.gui.page;

import io.github.hzhilong.base.bean.BuCallback;
import io.github.hzhilong.baseapp.utils.LayoutUtil;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.gui.component.BackupRestoreItemSelector;
import io.github.hzhilong.bilibili.backup.gui.component.UserSelector;
import io.github.hzhilong.bilibili.backup.gui.worker.ClearRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.DelaySetProcessingLoggerRunnable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;

/**
 * 清空页面
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class ClearPage extends PagePanel {

    public static final String BU_NAME = "清空";
    public static final String ACTIVE_BTN_NAME = "开始" + BU_NAME;
    public static final String INACTIVE_BTN_NAME = "取消" + BU_NAME;

    private UserSelector userSelector;

    private BackupRestoreItemSelector backupRestoreItemSelector;

    private JButton btnBackup;

    private JTextArea txtLog;

    private ClearRunnable clearRunnable;

    public ClearPage(Window parent, String appIconPath, OkHttpClient client) {
        super(parent, appIconPath, client);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initUI() {
        int posY = 0;

        userSelector = new UserSelector(parent, appIconPath, client);
        addFixedContent(userSelector, 0, posY++);
        addSeparatorToFixed(0, posY++);

        posY = 0;
        backupRestoreItemSelector = new BackupRestoreItemSelector(parent, appIconPath, null, 3);
        addDynamicContent(backupRestoreItemSelector, 0, posY++);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        GridBagConstraints temp = LayoutUtil.getSeparatorConstraints(0, posY++, 1, 1);
        addDynamicContent(btnPanel, temp);

        btnBackup = new JButton(ACTIVE_BTN_NAME);
        btnPanel.add(btnBackup);

        JScrollPane scrollPaneLog = addTxtLogToDynamic(0, posY++);
        txtLog = (JTextArea) scrollPaneLog.getViewport().getView();

        setDynamicContentVisible(false);
        initListener();
    }

    private void initListener() {
        userSelector.addActionListener(e -> setDynamicContentVisible(userSelector.getCurrUser() != null));
        btnBackup.addActionListener(e -> onBtnBackup());
    }

    private void onBtnBackup() {
        if (ACTIVE_BTN_NAME.equals(this.btnBackup.getText())) {
            if (GlobalState.getProcessing()) {
                JOptionPane.showMessageDialog(parent, "有其他任务在运行！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LinkedHashSet<BackupRestoreItem> items = backupRestoreItemSelector.getSelectedItems();
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "请至少选择一项！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int result = JOptionPane.showConfirmDialog(parent,
                    "是否开始" + BU_NAME + "？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                result = JOptionPane.showConfirmDialog(parent,
                        "是否开始" + BU_NAME + "？\n" +
                                "请注意：这会清空所选的数据，\n" +
                                "请检查数据是否已备份完成再继续。", "提示",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    result = JOptionPane.showConfirmDialog(parent,
                            "最后一次提示，是否清空所选的数据？", "提示",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        clearData(items);
                    }
                }
            }
        } else {
            int result = JOptionPane.showConfirmDialog(parent,
                    "正在进行" + BU_NAME + "，是否取消？", "提示", JOptionPane.YES_NO_OPTION);
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

    private void clearData(LinkedHashSet<BackupRestoreItem> items) {
        setBusyStatus(true);
        clearRunnable = new ClearRunnable(client, userSelector.getCurrUser(), items,
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
        new Thread(clearRunnable).start();
    }

    private void stopBackup() {
        log.info("中断任务中...");
        clearRunnable.setInterrupt(true);
    }
}