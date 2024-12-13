package io.github.hzhilong.bilibili.backup.gui.config;

import io.github.hzhilong.bilibili.backup.app.state.appdata.AppData;
import io.github.hzhilong.bilibili.backup.app.state.appdata.AppDataItem;

/**
 * 配置基类
 *
 * @author hzhilong
 * @version 1.0
 */
public abstract class BaseConfig {
    protected static AppData appData = AppData.getInstance();

    /**
     * 获取首选的值
     *
     * @param value         当前值
     * @param defaultValues 默认的值 依次获取，直至不为空
     * @param <D>           值的类型
     * @return 首选的值
     */
    @SafeVarargs
    public static <D> D getPreferredValue(D value, AppDataItem<D>... defaultValues) {
        if (value != null) {
            return value;
        }
        for (AppDataItem<D> item : defaultValues) {
            D defaultValue = item.getValue();
            if (defaultValue != null) {
                return defaultValue;
            }
        }
        return null;
    }
}
