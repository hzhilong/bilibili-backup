package io.github.hzhilong.bilibili.backup.app.state;

import io.github.hzhilong.base.utils.StringUtils;

/**
 * APP数据项
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppDataItem {

    private final PersistenceData persistenceData;
    private final String key;

    public AppDataItem(PersistenceData persistenceData, String key) {
        this(persistenceData, key, null);
    }

    public AppDataItem(PersistenceData persistenceData, String key, String defaultValue) {
        this.key = key;
        this.persistenceData = persistenceData;
        if (getValue() == null && defaultValue != null) {
            setValue(defaultValue);
        }
    }

    public String getValue() {
        return persistenceData.read(key);
    }

    public String getValue(String defaultValue) {
        String oldData = persistenceData.read(key);
        return StringUtils.notEmpty(oldData) ? oldData : defaultValue;
    }

    public String setValue(String value) {
        Object oldData = persistenceData.write(key, value);
        persistenceData.persistent();
        return oldData == null ? null : oldData.toString();
    }

    public String removeValue() {
        Object oldData = persistenceData.delete(key);
        persistenceData.persistent();
        return oldData == null ? null : oldData.toString();
    }

}
