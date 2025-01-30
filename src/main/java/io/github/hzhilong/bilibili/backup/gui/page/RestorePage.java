package io.github.hzhilong.bilibili.backup.gui.page;

import io.github.hzhilong.base.bean.BuCallback;
import io.github.hzhilong.baseapp.dialog.BaseLoadingDialog;
import io.github.hzhilong.baseapp.utils.LayoutUtil;
import io.github.hzhilong.bilibili.backup.app.bean.BackupDir;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.gui.component.BackupFileSelector;
import io.github.hzhilong.bilibili.backup.gui.component.BackupRestoreItemSelector;
import io.github.hzhilong.bilibili.backup.gui.component.UserSelector;
import io.github.hzhilong.bilibili.backup.gui.segment.SegmentUtil;
import io.github.hzhilong.bilibili.backup.gui.worker.BackupRestoreRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.DelaySetProcessingLoggerRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.RestoreRunnable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 备份页面
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class RestorePage extends PagePanel {

    public static final String BU_NAME = "还原";
    public static final String ACTIVE_BTN_NAME = "开始" + BU_NAME;
    public static final String INACTIVE_BTN_NAME = "取消" + BU_NAME;

    private UserSelector userSelector;

    private BackupFileSelector backupFileSelector;

    private BackupRestoreItemSelector backupRestoreItemSelector;

    private JButton btnRestore;

    private List<JRadioButton> segmentButtons;

    private JTextArea txtLog;

    private BackupRestoreRunnable restoreRunnable;

    private BaseLoadingDialog loadingDialog;

    public RestorePage(Window parent, String appIconPath, OkHttpClient client) {
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

        backupFileSelector = new BackupFileSelector(parent, appIconPath);
        addFixedContent(backupFileSelector, 0, posY++);
        addSeparatorToFixed(0, posY++);

        backupRestoreItemSelector = new BackupRestoreItemSelector(parent, appIconPath, null);
        addDynamicContent(backupRestoreItemSelector, 0, posY++);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        GridBagConstraints temp = LayoutUtil.getSeparatorConstraints(0, posY++, 1);
        addDynamicContent(btnPanel, temp);

        btnRestore = new JButton(ACTIVE_BTN_NAME);
        btnPanel.add(btnRestore);
        btnPanel.add(new JLabel("  分段还原的数量："));
        segmentButtons = SegmentUtil.createSegmentButtons(btnPanel);

        JScrollPane scrollPaneLog = addTxtLogToDynamic(0, posY++);
        txtLog = (JTextArea) scrollPaneLog.getViewport().getView();

        setDynamicContentVisible(false);

        loadingDialog = new BaseLoadingDialog(parent, AppConstant.APP_ICON, "提示", "解析备份数据中，请稍后...");

        initListener();
    }

    private void initListener() {
        btnRestore.addActionListener(e -> onBtnRestore());
        backupFileSelector.addActionListener(e -> {
            BackupDir currBackupDir = backupFileSelector.getCurrBackupDir();
            if (currBackupDir == null) {
                setDynamicContentVisible(false);
            } else {
//                    showLoadingDialog(true);
                backupRestoreItemSelector.refreshItems(currBackupDir);
                setDynamicContentVisible(true);
//                    showLoadingDialog(false);
            }
        });
    }

    private void showLoadingDialog(boolean flag) {
        if (flag) {
            loadingDialog.showDialog();
        } else {
            loadingDialog.closeDialog(500);
        }
    }

    private void onBtnRestore() {
        if (ACTIVE_BTN_NAME.equals(this.btnRestore.getText())) {
            if (GlobalState.getProcessing()) {
                JOptionPane.showMessageDialog(parent, "有其他任务在运行！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SavedUser currUser = userSelector.getCurrUser();
            if (currUser == null) {
                JOptionPane.showMessageDialog(parent, "请选择新账号！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LinkedHashSet<BackupRestoreItem> items = backupRestoreItemSelector.getSelectedItems();
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "请至少选择一项！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int result = JOptionPane.showConfirmDialog(parent, "是否开始" + BU_NAME + "？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                restore(items);
            }
        } else {
            int result = JOptionPane.showConfirmDialog(parent, "正在进行" + BU_NAME + "，是否取消？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                stopRestore();
            }
        }
    }

    private void setBusyStatus(boolean flag) {
        if (flag) {
            this.btnRestore.setText(INACTIVE_BTN_NAME);
        } else {
            this.btnRestore.setText(ACTIVE_BTN_NAME);
        }
        userSelector.setEnabled(!flag);
        backupFileSelector.setEnabled(!flag);
        backupRestoreItemSelector.setEnabled(!flag);
        if (flag) {
            GlobalState.setProcessingLogger(txtLog);
        } else {
            new Thread(new DelaySetProcessingLoggerRunnable(null)).start();
        }
        GlobalState.setProcessing(flag);
    }

    private void restore(LinkedHashSet<BackupRestoreItem> items) {
        setBusyStatus(true);
        restoreRunnable = new RestoreRunnable(client, userSelector.getCurrUser(), items,
                backupFileSelector.getCurrBackupDir().getDirFile().getPath(),
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
        // 分段处理
        SegmentUtil.handle(this, BusinessType.RESTORE, restoreRunnable, segmentButtons);
        new Thread(restoreRunnable).start();
    }

    private void stopRestore() {
        log.info("中断任务中...");
        restoreRunnable.setInterrupt(true);
    }
}