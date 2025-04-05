package io.github.hzhilong.bilibili.backup.gui.frame;

import io.github.hzhilong.baseapp.component.BaseFrame;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.gui.page.CopyFavPage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.awt.*;

/**
 * 复制收藏夹的窗口
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class CopyFavFrame extends BaseFrame {

    public static final String NAME = "复制收藏夹";
    private String cookie;
    private OkHttpClient client;

    public CopyFavFrame(Window parent, String cookie, OkHttpClient client) {
        super(parent, AppConstant.APP_ICON, new Dimension(1300, 800), NAME);
        this.cookie = cookie;
        this.client = client;
        init();
    }

    public void init() {
        setLayout(new GridBagLayout());
        add(new CopyFavPage(this, appIconPath, client, cookie), new GridBagConstraints(0, 0, 1, 1,
                1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
    }
}
