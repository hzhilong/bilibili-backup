package io.github.hzhilong.bilibili.backup.app.state.appdata;

import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.state.PersistenceData;
import io.github.hzhilong.bilibili.backup.gui.segment.SegmentConfig;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * APP数据
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppData extends Properties implements PersistenceData {

    private static final String CONFIG_PATH = "bin/app.data";

    private AppData() {
        File file = new File(CONFIG_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (InputStreamReader inputStream = new InputStreamReader(Files.newInputStream(file.toPath()))) {
            this.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String read(String key) {
        return getProperty(key);
    }

    @Override
    public Object write(String key, String value) {
        return setProperty(key, value);
    }

    @Override
    public Object delete(String key) {
        return remove(key);
    }

    @Override
    public void persistent() {
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(Paths.get(CONFIG_PATH)))) {
            store(writer, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Holder {
        private final static AppData INSTANCE = new AppData();
    }

    public static AppData getInstance() {
        return AppData.Holder.INSTANCE;
    }

    public Dimension getWindowsLocation() {
        Integer x = AppDataItem.WINDOW_LOCATION_X.getValue();
        if (x == null) {
            return null;
        }
        Integer y = AppDataItem.WINDOW_LOCATION_Y.getValue();
        if (y == null) {
            return null;
        }
        return new Dimension(x, y);
    }

    public Dimension getWindowsSize() {
        Integer w = AppDataItem.WINDOW_LOCATION_W.getValue();
        if (w == null) {
            return null;
        }
        Integer h = AppDataItem.WINDOW_LOCATION_H.getValue();
        if (h == null) {
            return null;
        }
        return new Dimension(w, h);
    }

    public void setWindowsXY(int x, int y) {
        AppDataItem.WINDOW_LOCATION_X.setValue(x);
        AppDataItem.WINDOW_LOCATION_Y.setValue(y);
    }

    public void setWindowsSize(int w, int h) {
        AppDataItem.WINDOW_LOCATION_W.setValue(w);
        AppDataItem.WINDOW_LOCATION_H.setValue(h);
    }

    /**
     * 获取存在的分段处理配置
     */
    public SegmentConfig getSegmentConfig(String uid, BusinessType businessType, BackupRestoreItem backupRestoreItem) {
        String property = read(SegmentConfig.getAppPropertyKey(uid, businessType, backupRestoreItem));
        if (StringUtils.isEmpty(property)) {
            return null;
        }
        return SegmentConfig.parse(uid, businessType, backupRestoreItem, property);
    }

    public void setSegmentConfig(SegmentConfig segmentConfig) {
        write(segmentConfig.getAppPropertyKey(), segmentConfig.getAppPropertyValue());
        persistent();
    }

    public void delSegmentConfig(SegmentConfig segmentConfig) {
        remove(segmentConfig.getAppPropertyKey());
        persistent();
    }

    public static void initAllData() {
        AppDataItem<Integer> initialFontSize = AppDataItem.INITIAL_FONT_SIZE;
        Integer initialFontSizeValue = initialFontSize.getValue();
        if (initialFontSizeValue == null) {
            initialFontSize.setValue(UIManager.getFont("defaultFont").getSize());
        }
        AppDataItem<String> initialFontFamily = AppDataItem.INITIAL_FONT_FAMILY;
        String initialFontFamilyValue = initialFontFamily.getValue();
        if (initialFontFamilyValue == null) {
            initialFontFamily.setValue(UIManager.getFont("Label.font").getFamily());
        }
    }

}
