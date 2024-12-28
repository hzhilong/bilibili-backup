package io.github.hzhilong.bilibili.backup.gui.page;

import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.gui.component.PagePanel;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * 主界面tab栏
 *
 * @author hzhilong
 * @version 1.0
 */
public class MainTabbedPane extends JTabbedPane {

    public MainTabbedPane() {
        initUI();
    }

    public void initUI() {
        OkHttpClient client = GlobalState.CLIENT;
        addView("备份", new BackupPage(client));
        addView("还原", new RestorePage(client));
        addView("清空", new ClearPage(client));
        addView("已注销账号数据", new CancelledAccountPage(client));
        addView("其他工具", new ToolsPage(client));
    }

    private void addView(String title, PagePanel page) {
        add(title, page);
        page.setBorder(new EmptyBorder(10, 10, 10, 10));
    }
}
