package io.github.hzhilong.bilibili.backup.gui.page;

import io.github.hzhilong.bilibili.backup.gui.component.LinkLabel;
import io.github.hzhilong.bilibili.backup.gui.component.PagePanel;
import io.github.hzhilong.bilibili.backup.app.config.AppBuildProperties;
import okhttp3.OkHttpClient;

import javax.swing.*;

/**
 * 关于页面
 *
 * @author hzhilong
 * @version 1.0
 */
public class AboutPage extends PagePanel {

    private AppBuildProperties appProperties;

    public AboutPage(OkHttpClient client) {
        super(client);
    }

    @Override
    public void initData() {
        appProperties = AppBuildProperties.getInstance();
    }

    @Override
    public void initUI() {
        int posY = 0;
        addFixedContent(new JLabel("名称：" + appProperties.getName()), 0, posY++);
        addFixedContent(new JLabel("描述：" + appProperties.getDescription()), 0, posY++);
        addFixedContent(new JLabel("版本：" + appProperties.getVersion()), 0, posY++);
        addFixedContent(new JLabel("作者：" + appProperties.getAuthor()), 0, posY++);
        addFixedContent(new LinkLabel("开源：", appProperties.getGithub()), 0, posY++);
    }

}
