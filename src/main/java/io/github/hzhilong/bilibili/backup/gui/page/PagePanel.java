package io.github.hzhilong.bilibili.backup.gui.page;

import io.github.hzhilong.baseapp.component.BasePagePanel;
import okhttp3.OkHttpClient;

import java.awt.*;

/**
 * 应用页面
 *
 * @author hzhilong
 * @version 1.0
 */
public class PagePanel extends BasePagePanel {

    protected OkHttpClient client;

    public PagePanel(Window parent, String appIconPath, OkHttpClient client) {
        super(parent, appIconPath);
        this.client = client;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initUI() {

    }
}
