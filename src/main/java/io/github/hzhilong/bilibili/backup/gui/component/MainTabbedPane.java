package io.github.hzhilong.bilibili.backup.gui.component;

import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.gui.page.BackupPage;
import io.github.hzhilong.bilibili.backup.gui.page.CancelledAccountPage;
import io.github.hzhilong.bilibili.backup.gui.page.ClearPage;
import io.github.hzhilong.bilibili.backup.gui.page.PagePanel;
import io.github.hzhilong.bilibili.backup.gui.page.RestorePage;
import io.github.hzhilong.bilibili.backup.gui.page.ToolsPage;
import okhttp3.OkHttpClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * 主界面tab栏
 *
 * @author hzhilong
 * @version 1.0
 */
public class MainTabbedPane extends JTabbedPane {

    private JFrame frame;

    public MainTabbedPane(JFrame frame) {
        this.frame = frame;
        initUI();
    }

    public void initUI() {
        OkHttpClient client = GlobalState.CLIENT;
        String appIcon = AppConstant.APP_ICON;
        addView("备份", new BackupPage(frame, appIcon, client));
        addView("还原", new RestorePage(frame, appIcon, client));
        addView("清空", new ClearPage(frame, appIcon, client));
        addView("已注销账号数据", new CancelledAccountPage(frame, appIcon, client));
        addView("其他工具", new ToolsPage(frame, appIcon, client));
    }

    private void addView(String title, PagePanel page) {
        add(title, page);
        page.setBorder(new EmptyBorder(10, 10, 10, 10));
    }
}
