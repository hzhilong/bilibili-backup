package top.ybgnb.bilibili.backup.ui.page;

import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.ui.component.ComponentInit;
import top.ybgnb.bilibili.backup.ui.component.PagePanel;
import top.ybgnb.bilibili.backup.ui.state.GlobalState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @ClassName MainTabbedPane
 * @Description 主界面tab布局
 * @Author hzhilong
 * @Time 2024/11/27
 * @Version 1.0
 */
public class MainTabbedPane extends JTabbedPane implements ComponentInit {


    @Override
    public void initData() throws BusinessException {

    }

    @Override
    public void initUI() throws BusinessException {
        OkHttpClient client = GlobalState.CLIENT;
        addView("备份", new BackupPage(client));
        addView("还原", new RestorePage(client));
        addView("已读所有消息", new ReadAllMessagePage(client));
        addView("已注销账号数据", new CancelledAccountPage(client));
        addView("关于", new AboutPage(client));
    }

    private void addView(String title, PagePanel page) throws BusinessException {
        add(title, page);
        page.setBorder(new EmptyBorder(10, 10, 10, 10));
        page.init();
    }
}
