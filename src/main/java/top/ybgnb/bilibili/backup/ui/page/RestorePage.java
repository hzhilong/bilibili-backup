package top.ybgnb.bilibili.backup.ui.page;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.ui.bean.BackupDir;
import top.ybgnb.bilibili.backup.ui.component.BackupFileSelector;
import top.ybgnb.bilibili.backup.ui.component.BackupRestoreItemSelector;
import top.ybgnb.bilibili.backup.ui.component.LoadingDialog;
import top.ybgnb.bilibili.backup.ui.component.PagePanel;
import top.ybgnb.bilibili.backup.ui.component.UserSelector;
import top.ybgnb.bilibili.backup.ui.state.GlobalState;
import top.ybgnb.bilibili.backup.ui.worker.BackupRestoreRunnable;
import top.ybgnb.bilibili.backup.ui.worker.BuCallback;
import top.ybgnb.bilibili.backup.ui.worker.DelaySetProcessingLoggerRunnable;
import top.ybgnb.bilibili.backup.ui.worker.RestoreRunnable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;

/**
 * @ClassName RestorePage
 * @Description 备份页面
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
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

    private JTextArea txtLog;

    private JScrollPane scrollPaneLog;

    private BackupRestoreRunnable restoreRunnable;

    private LoadingDialog loadingDialog;

    public RestorePage(OkHttpClient client) {
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

        backupFileSelector = new BackupFileSelector();
        addFixedContent(backupFileSelector, 0, posY++);
        addSeparatorToFixed(0, posY++);

        backupRestoreItemSelector = new BackupRestoreItemSelector(null);
        addDynamicContent(backupRestoreItemSelector, 0, posY++);

        btnRestore = new JButton(ACTIVE_BTN_NAME);
        addDynamicContent(btnRestore, 0, posY++);

        scrollPaneLog = addTxtLogToDynamic(0, posY++);
        txtLog = (JTextArea) scrollPaneLog.getViewport().getView();

        setDynamicContentVisible(false);

        loadingDialog = new LoadingDialog("解析备份数据中，请稍后...");

        initListener();
    }

    private void initListener() {
        userSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userSelector.getCurrUser() != null) {
                } else {
                }
            }
        });
        btnRestore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBtnRestore();
            }
        });
        backupFileSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BackupDir currBackupDir = backupFileSelector.getCurrBackupDir();
                if (currBackupDir == null) {
                    setDynamicContentVisible(false);
                } else {
//                    showLoadingDialog(true);
                    backupRestoreItemSelector.refreshItems(currBackupDir);
                    setDynamicContentVisible(true);
//                    showLoadingDialog(false);
                }
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
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "有其他任务在运行！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SavedUser currUser = userSelector.getCurrUser();
            if (currUser == null) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请选择新账号！", "提示", JOptionPane.OK_OPTION);
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
                restore();
            }
        } else {
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "正在进行" + BU_NAME + "，是否取消？", "提示",
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

    private void restore() {
        setBusyStatus(true);
        restoreRunnable = new RestoreRunnable(client, userSelector.getCurrUser(),
                backupRestoreItemSelector.getSelectedItems(),
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
        new Thread(restoreRunnable).start();
    }

    private void stopRestore() {
        log.info("中断任务中...");
        restoreRunnable.setInterrupt(true);
    }
}