package io.github.hzhilong.bilibili.backup.app.service;

import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.user.User;

/**
 * 服务构建
 *
 * @author hzhilong
 * @version 1.0
 */
public interface ServiceBuilder {
    BackupRestoreService build(OkHttpClient client, User user, String path);
}
