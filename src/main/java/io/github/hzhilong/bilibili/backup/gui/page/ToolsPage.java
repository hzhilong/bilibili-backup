package io.github.hzhilong.bilibili.backup.gui.page;

import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.utils.JContextUtil;
import io.github.hzhilong.baseapp.utils.LayoutUtil;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.gui.component.UserSelector;
import io.github.hzhilong.bilibili.backup.gui.frame.ViewDMFrame;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.AutoAnsweringRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.CopyFavRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.DelaySetProcessingLoggerRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.BackupDMRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.FavAllVideosRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.OpenAutoReplyRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.OpenFrameRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.RemoveScamFollowerRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.RunnableBuilder;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.SessionRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.Tool;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.ToolBuCallback;
import io.github.hzhilong.bilibili.backup.gui.worker.tools.ToolRunnable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private List<Tool> tools;
    private Map<String, ToolRunnable<?, ?>> toolsRunnable;
    private Map<Tool, JButton> toolsBtn;

    public ToolsPage(Window parent, String appIconPath, OkHttpClient client) {
        super(parent, appIconPath, client);
    }

    @Override
    public void initData() {
        tools = new ArrayList<>();
        toolsRunnable = new HashMap<>();
        toolsBtn = new HashMap<>();
        tools.add(new Tool("已读所有私信", "还原[关注]后会有很多私信，使用该工具可以一键已读。",
                (client, user, buCallback)
                        -> new SessionRunnable(client, user, buCallback, SessionRunnable.TYPE_READ_ALL_SESSION)));
        tools.add(new Tool("删除所有私信", "一键删除B站所有私信。",
                (client, user, buCallback)
                        -> new SessionRunnable(client, user, buCallback, SessionRunnable.TYPE_DELETE_ALL_SESSION)));
        tools.add(new Tool("删除所有系统通知", "一键删除B站系统通知。",
                (client, user, buCallback)
                        -> new SessionRunnable(client, user, buCallback, SessionRunnable.TYPE_DELETE_ALL_SYS_MSG)));
        tools.add(new Tool("强开私信自动回复功能", "B站达到1000粉丝才能开启消息自动回复的功能",
                (client, user, buCallback)
                        -> new OpenAutoReplyRunnable(client, user, buCallback, true)));
        tools.add(new Tool("关闭私信自动回复功能", "B站达到1000粉丝才能开启消息自动回复的功能",
                (client, user, buCallback)
                        -> new OpenAutoReplyRunnable(client, user, buCallback, false)));

        tools.add(new Tool("备份视频弹幕", "备份视频弹幕，弹幕实际数量可能比视频页面显示的弹幕数少。支持反查发送者。",
                BackupDMRunnable::new, false));
        tools.add(new Tool(ViewDMFrame.NAME, "查看已备份的弹幕文件，方便在反查发送者的同时，过滤掉不存在的uid", new RunnableBuilder() {
            @Override
            public ToolRunnable build(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback) {
                return new OpenFrameRunnable(client, user, buCallback, new ViewDMFrame(parentWindow, userSelector.getCurrUser().getCookie(), client));
            }
        }, false));
        tools.add(new Tool("批量拷贝收藏夹", "批量拷贝他人公开的收藏夹，亦可用于快速还原。",
                CopyFavRunnable::new, false));
        tools.add(new Tool("移除粉丝中的片姐", "2025.4 出现一大堆自动关注别人的片姐，被关注后可能导致自己的账号被大量举报/警告/封禁",
                RemoveScamFollowerRunnable::new, true, "是否一键移除粉丝中的片姐？（低等级+大量关注+没有其他活动痕迹）"));
        tools.add(new Tool("收藏所有投稿", "收藏其他人投稿的所有视频",
                FavAllVideosRunnable::new, false));
        tools.add(new Tool("AI转正答题", "使用AI自动完成转正答题",
                AutoAnsweringRunnable::new, false));
    }

    @Override
    public void initUI() {
        int posY = 0;

        userSelector = new UserSelector(parentWindow, appIconPath, client);
        addFixedContent(userSelector, 0, posY++);
        addSeparatorToFixed(0, posY++);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridBagLayout());
        addDynamicContent(btnPanel, 0, posY++);

        int column = 5;
        for (int i = 0; i < tools.size(); i++) {
            Tool tool = tools.get(i);
            String name = tool.getName();
            RunnableBuilder builder = tool.getRunnableBuilder();
            JButton button = new JButton(tool.getName());
            button.setToolTipText(tool.getDesc());
            LayoutUtil.addGridBar(btnPanel, button, i % column, (i / column) + posY);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startRunnable(button, name, builder, tool.isTip(), tool.getTipMsg());
                }
            });
            toolsBtn.put(tool, button);
        }
        posY++;

        JScrollPane scrollPaneLog = addTxtLogToDynamic(0, posY++);
        txtLog = (JTextArea) scrollPaneLog.getViewport().getView();

        setDynamicContentVisible(false);
        userSelector.addActionListener(e -> setDynamicContentVisible(userSelector.getCurrUser() != null));
    }


    private void startRunnable(JButton button, String name, RunnableBuilder builder, boolean isTip, String tipMsg) {
        if (button.getText().startsWith(INACTIVE_BTN_NAME_BEGIN)) {
            // 停止
            int result = JOptionPane.showConfirmDialog(parentWindow, "正在进行" + name + "，是否取消？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                log.info("中断任务中...");
                ToolRunnable runnable = toolsRunnable.get(name);
                runnable.setInterrupt(true);
            }
        } else {
            // 开始
            if (GlobalState.getProcessing()) {
                JOptionPane.showMessageDialog(parentWindow, "有其他任务在运行！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SavedUser currUser = userSelector.getCurrUser();
            if (currUser == null) {
                JOptionPane.showMessageDialog(parentWindow, "请选择账号！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int result = JOptionPane.YES_OPTION;
            if (isTip) {
                if (StringUtils.isEmpty(tipMsg)) {
                    tipMsg = "是否开始" + name + "？";
                }
                result = JOptionPane.showConfirmDialog(parentWindow, tipMsg, "提示",
                        JOptionPane.YES_NO_OPTION);
            }
            if (result == JOptionPane.YES_OPTION) {
                setBusyStatus(button, name, true);
                ToolRunnable toolRunnable = builder.build(client, userSelector.getCurrUser(),
                        new ToolBuCallback<Void>(button, name) {
                            @Override
                            public void update(JButton button, String name, boolean busyStatus) {
                                setBusyStatus(button, name, busyStatus);
                            }
                        });
                JContextUtil.init(this, toolRunnable);
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
        for (Map.Entry<Tool, JButton> entry : toolsBtn.entrySet()) {
            JButton btn = entry.getValue();
            if (btn != button) {
                btn.setEnabled(!flag);
            }
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