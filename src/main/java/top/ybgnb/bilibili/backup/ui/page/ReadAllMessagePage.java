package top.ybgnb.bilibili.backup.ui.page;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.ui.component.PagePanel;
import top.ybgnb.bilibili.backup.ui.component.UserSelector;
import top.ybgnb.bilibili.backup.ui.state.GlobalState;
import top.ybgnb.bilibili.backup.ui.worker.BuCallback;
import top.ybgnb.bilibili.backup.ui.worker.DelaySetProcessingLoggerRunnable;
import top.ybgnb.bilibili.backup.ui.worker.ReadAllSessionRunnable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @ClassName ReadAllMessagePage
 * @Description 已读所有消息页面
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public class ReadAllMessagePage extends PagePanel {

    public static final String BU_NAME = "已读所有消息";
    public static final String ACTIVE_BTN_NAME = "开始" + BU_NAME;
    public static final String INACTIVE_BTN_NAME = "取消" + BU_NAME;

    private UserSelector userSelector;

    private JButton btnStart;

    private JTextArea txtLog;

    private JScrollPane scrollPaneLog;

    private ReadAllSessionRunnable readAllSessionRunnable;

    public ReadAllMessagePage(OkHttpClient client) {
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

        btnStart = new JButton(ACTIVE_BTN_NAME);
        addDynamicContent(btnStart, 0, posY++);

        scrollPaneLog = addTxtLogToDynamic(0, posY++);
        txtLog = (JTextArea) scrollPaneLog.getViewport().getView();

        setDynamicContentVisible(false);
        initListener();
    }

    private void initListener() {
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBtnStart();
            }
        });
        userSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDynamicContentVisible(userSelector.getCurrUser() != null);
            }
        });
    }

    private void onBtnStart() {
        if (ACTIVE_BTN_NAME.equals(this.btnStart.getText())) {
            if (GlobalState.getProcessing()) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "有其他任务在运行！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SavedUser currUser = userSelector.getCurrUser();
            if (currUser == null) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请选择账号！", "提示", JOptionPane.OK_OPTION);
                return;
            }
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "是否开始" + BU_NAME + "？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                readAllSession();
            }
        } else {
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "正在进行" + BU_NAME + "，是否取消？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                stopReadAllSession();
            }
        }
    }

    private void setBusyStatus(boolean flag) {
        if (flag) {
            this.btnStart.setText(INACTIVE_BTN_NAME);
        } else {
            this.btnStart.setText(ACTIVE_BTN_NAME);
        }
        userSelector.setEnabled(!flag);
        if (flag) {
            GlobalState.setProcessingLogger(txtLog);
        } else {
            new Thread(new DelaySetProcessingLoggerRunnable(null)).start();
        }
        GlobalState.setProcessing(flag);
    }

    private void readAllSession() {
        setBusyStatus(true);
        readAllSessionRunnable = new ReadAllSessionRunnable(client, userSelector.getCurrUser(),
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
        new Thread(readAllSessionRunnable).start();
    }

    private void stopReadAllSession() {
        log.info("中断任务中...");
        readAllSessionRunnable.setInterrupt(true);
    }
}