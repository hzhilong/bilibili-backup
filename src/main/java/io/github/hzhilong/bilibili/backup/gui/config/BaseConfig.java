package io.github.hzhilong.bilibili.backup.gui.config;

import io.github.hzhilong.bilibili.backup.app.state.AppData;

/**
 * 配置基类
 *
 * @author hzhilong
 * @version 1.0
 */
public abstract class BaseConfig {
    protected static AppData appData = AppData.getInstance();
}
