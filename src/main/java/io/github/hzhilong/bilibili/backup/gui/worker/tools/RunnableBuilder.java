package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import okhttp3.OkHttpClient;

/**
 * 服务构建
 *
 * @author hzhilong
 * @version 1.0
 */
public interface RunnableBuilder {
    ToolRunnable build(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback);
}
