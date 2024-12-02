package top.ybgnb.bilibili.backup.ui.page;

import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.ui.component.LinkLabel;
import top.ybgnb.bilibili.backup.ui.component.PagePanel;
import top.ybgnb.bilibili.backup.ui.config.AppProperties;

import javax.swing.*;

/**
 * @ClassName AboutPage
 * @Description 关于
 * @Author hzhilong
 * @Time 2024/11/27
 * @Version 1.0
 */
public class AboutPage extends PagePanel {

    private AppProperties appProperties;

    public AboutPage(OkHttpClient client) {
        super(client);
    }

    @Override
    public void initData() throws BusinessException {
        appProperties = AppProperties.getInstance();
    }

    @Override
    public void initUI() throws BusinessException {
        super.initUI();
        int posY = 0;
        addFixedContent(new JLabel("名称：" + appProperties.getName()), 0, posY++);
        addFixedContent(new JLabel("描述：" + appProperties.getDescription()), 0, posY++);
        addFixedContent(new JLabel("版本：" + appProperties.getVersion()), 0, posY++);
        addFixedContent(new JLabel("作者：" + appProperties.getAuthor()), 0, posY++);
        addFixedContent(new LinkLabel("开源：", appProperties.getGithub()), 0, posY++);
    }

}
