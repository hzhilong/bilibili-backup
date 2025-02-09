package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.baseapp.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;

/**
 * 打开窗口的线程
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class OpenFrameRunnable extends ToolRunnable<BaseService, Void> implements NeedContext {

    @Setter
    private Window parentWindow;
    @Setter
    private String appIconPath;

    private JFrame frame;

    public OpenFrameRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback, JFrame frame) {
        super(client, user, buCallback);
        this.frame = frame;
    }

    @Override
    protected void newServices(LinkedHashSet<BaseService> services) {
    }

    @Override
    protected Void runTool() throws BusinessException {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });
        return null;
    }

}
