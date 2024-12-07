package top.ybgnb.bilibili.backup.ui.page;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.business.BusinessType;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreItem;
import top.ybgnb.bilibili.backup.ui.component.BackupRestoreItemSelector;
import top.ybgnb.bilibili.backup.ui.component.PagePanel;
import top.ybgnb.bilibili.backup.ui.component.UserSelector;
import top.ybgnb.bilibili.backup.ui.state.GlobalState;
import top.ybgnb.bilibili.backup.ui.utils.LayoutUtil;
import top.ybgnb.bilibili.backup.ui.segment.SegmentUtil;
import top.ybgnb.bilibili.backup.ui.worker.BackupRunnable;
import top.ybgnb.bilibili.backup.ui.worker.BuCallback;
import top.ybgnb.bilibili.backup.ui.worker.DelaySetProcessingLoggerRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.List;

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

    private List<JRadioButton> segmentButtons;

    private JTextArea txtLog;

    private JScrollPane scrollPaneLog;

    private BackupRunnable backupRunnable;

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

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        GridBagConstraints temp = LayoutUtil.getSeparatorConstraints(0, posY++, 1);
        addDynamicContent(btnPanel, temp);

        btnBackup = new JButton(ACTIVE_BTN_NAME);
        btnPanel.add(btnBackup);
        btnPanel.add(new JLabel("  分段备份的数量："));
        segmentButtons = SegmentUtil.createSegmentButtons(btnPanel);

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
            LinkedHashSet<BackupRestoreItem> items = backupRestoreItemSelector.getSelectedItems();
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请至少选择一项！", "提示", JOptionPane.OK_OPTION);
                return;
            }
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "是否开始" + BU_NAME + "？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                backup(items);
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

    private void backup(LinkedHashSet<BackupRestoreItem> items) {
        setBusyStatus(true);
        backupRunnable = new BackupRunnable(client, userSelector.getCurrUser(), items,
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
        SegmentUtil.handle(this, BusinessType.BACKUP, backupRunnable, segmentButtons);
        new Thread(backupRunnable).start();
    }

    private void stopBackup() {
        log.info("中断任务中...");
        backupRunnable.setInterrupt(true);
    }
}