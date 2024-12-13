package io.github.hzhilong.bilibili.backup.gui.page;

import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.gui.component.PagePanel;
import io.github.hzhilong.bilibili.backup.gui.component.UserSelector;
import io.github.hzhilong.bilibili.backup.gui.worker.DelaySetProcessingLoggerRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.OpenAutoReplyRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.ReadAllSessionRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.RunnableBuilder;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.ToolBuCallback;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.ToolRunnable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 工具箱页面
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class ToolsPage extends PagePanel {

    private final String INACTIVE_BTN_NAME_BEGIN = "停止";

    private UserSelector userSelector;

    private JTextArea txtLog;

    private LinkedHashMap<String, RunnableBuilder> tools;
    private Map<String, ToolRunnable<?, ?>> toolsRunnable;

    public ToolsPage(OkHttpClient client) {
        super(client);
    }

    @Override
    public void initData() {
        this.tools = new LinkedHashMap<>();
        this.toolsRunnable = new HashMap<>();
        this.tools.put("已读所有消息", ReadAllSessionRunnable::new);
        this.tools.put("强开私信自动回复功能", (client, user, buCallback) -> new OpenAutoReplyRunnable(client, user, buCallback, true));
        this.tools.put("关闭私信自动回复功能", (client, user, buCallback) -> new OpenAutoReplyRunnable(client, user, buCallback, false));
    }

    @Override
    public void initUI() {
        int posY = 0;

        userSelector = new UserSelector(client);
        addFixedContent(userSelector, 0, posY++);
        addSeparatorToFixed(0, posY++, 3);

        int pos = 0;
        for (Map.Entry<String, RunnableBuilder> entry : this.tools.entrySet()) {
            String name = entry.getKey();
            RunnableBuilder builder = entry.getValue();
            JButton button = new JButton(name);
            addDynamicContent(button, pos % 3, (pos / 3) + posY);
            pos++;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startRunnable(button, name, builder);
                }
            });
        }
        posY++;

        JScrollPane scrollPaneLog = addTxtLogToDynamic(0, posY++, 3);
        txtLog = (JTextArea) scrollPaneLog.getViewport().getView();

        setDynamicContentVisible(false);
        userSelector.addActionListener(e -> setDynamicContentVisible(userSelector.getCurrUser() != null));
    }


    private void startRunnable(JButton button, String name, RunnableBuilder builder) {
        if (button.getText().startsWith(INACTIVE_BTN_NAME_BEGIN)) {
            // 停止
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "正在进行" + name + "，是否取消？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                log.info("中断任务中...");
                ToolRunnable runnable = toolsRunnable.get(name);
                runnable.setInterrupt(true);
            }
        } else {
            // 开始
            if (GlobalState.getProcessing()) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "有其他任务在运行！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SavedUser currUser = userSelector.getCurrUser();
            if (currUser == null) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请选择账号！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                    "是否开始" + name + "？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                setBusyStatus(button, name, true);
                ToolRunnable toolRunnable = builder.build(client, userSelector.getCurrUser(),
                        new ToolBuCallback<Void>(button, name) {
                            @Override
                            public void update(JButton button, String name, boolean busyStatus) {
                                setBusyStatus(button, name, busyStatus);
                            }
                        });
                toolsRunnable.put(name, toolRunnable);
                new Thread(toolRunnable).start();
            }
        }
    }

    private void setBusyStatus(JButton button, String name, boolean flag) {
        if (flag) {
            button.setText(INACTIVE_BTN_NAME_BEGIN + name);
        } else {
            button.setText(name);
        }
        userSelector.setEnabled(!flag);
        if (flag) {
            GlobalState.setProcessingLogger(txtLog);
        } else {
            new Thread(new DelaySetProcessingLoggerRunnable(null)).start();
        }
        GlobalState.setProcessing(flag);
    }


}