package io.github.hzhilong.bilibili.backup.app.state.appdata;

import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.app.state.PersistenceData;
import lombok.Getter;

/**
 * APP数据项
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppDataItem<D> {
    public static AppDataItem<Boolean> IS_FIRST_RUN
            = new AppDataItem<>("first.run", new BooleanItemValue());
    public static AppDataItem<Integer> WINDOW_LOCATION_X
            = new AppDataItem<>("window.location.x", new IntegerItemValue());
    public static AppDataItem<Integer> WINDOW_LOCATION_Y
            = new AppDataItem<>("window.location.y", new IntegerItemValue());
    public static AppDataItem<Integer> WINDOW_LOCATION_W
            = new AppDataItem<>("window.location.w", new IntegerItemValue());
    public static AppDataItem<Integer> WINDOW_LOCATION_H
            = new AppDataItem<>("window.location.h", new IntegerItemValue());

    public static AppDataItem<Integer> INITIAL_FONT_SIZE
            = new AppDataItem<>("initial.font.size", new IntegerItemValue());
    public static AppDataItem<String> INITIAL_FONT_FAMILY
            = new AppDataItem<>("initial.font.family", new StringItemValue());
    public static AppDataItem<Integer> FONT_SIZE
            = new AppDataItem<>("font.size", new IntegerItemValue());
    public static AppDataItem<String> FONT_FAMILY
            = new AppDataItem<>("font.family", new StringItemValue());
    public static AppDataItem<String> THEME
            = new AppDataItem<>("theme", new StringItemValue());
    public static AppDataItem<Boolean> ALLOW_FAILURE
            = new AppDataItem<>("setting.allow-failure", new BooleanItemValue(false));
    public static AppDataItem<Boolean> DIRECT_RESTORE
            = new AppDataItem<>("setting.direct-restore", new BooleanItemValue(false));

    private final PersistenceData persistenceData;
    private final String key;
    private final ItemValue<D> itemValue;
    @Getter
    private final D defaultValue;

    AppDataItem(String key, ItemValue<D> itemValue) {
        this.persistenceData = AppData.getInstance();
        this.key = key;
        this.itemValue = itemValue;
        this.defaultValue = itemValue.defaultValue();
        if (getValue() == null && defaultValue != null) {
            setValue(defaultValue);
        }
    }

    public D getValue() {
        return itemValue.parseData(persistenceData.read(key));
    }

    public D getValue(D defaultValue) {
        String oldData = persistenceData.read(key);
        if (StringUtils.notEmpty(oldData)) {
            return itemValue.parseData(oldData);
        } else {
            return defaultValue;
        }
    }

    public String setValue(D value) {
        Object oldData = persistenceData.write(key, itemValue.persistenceData(value));
        persistenceData.persistent();
        return oldData == null ? null : oldData.toString();
    }

    public String removeValue() {
        Object oldData = persistenceData.delete(key);
        persistenceData.persistent();
        return oldData == null ? null : oldData.toString();
    }

}
