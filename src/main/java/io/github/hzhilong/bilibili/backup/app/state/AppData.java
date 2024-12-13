package io.github.hzhilong.bilibili.backup.app.state;

import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
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

    public static final String KEY_IS_FIRST_RUN = "first.run";

    public static final String KEY_WINDOWS_X = "windows.x";
    public static final String KEY_WINDOWS_Y = "windows.y";
    public static final String KEY_WINDOWS_W = "windows.w";
    public static final String KEY_WINDOWS_H = "windows.h";

    public static final String KEY_INITIAL_FONT_SIZE = "initial.font.size";
    public static final String KEY_INITIAL_FONT_FAMILY = "initial.font.family";
    public static final String KEY_FONT_SIZE = "font.size";
    public static final String KEY_FONT_FAMILY = "font.family";
    public static final String KEY_FONT_THEME = "theme";

    /**
     * 未完成的分段备份 key+备份目录+备份项目名<->待备份的开始页码
     */
    public static final String KEY_BACKUP_SEGMENT = "backup.segment.";

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
        String propertyX = read(KEY_WINDOWS_X);
        if (StringUtils.isEmpty(propertyX)) {
            return null;
        }
        String propertyY = read(KEY_WINDOWS_Y);
        if (StringUtils.isEmpty(propertyY)) {
            return null;
        }
        return new Dimension(Integer.parseInt(propertyX), Integer.parseInt(propertyY));
    }

    public Dimension getWindowsSize() {
        String propertyW = read(KEY_WINDOWS_W);
        if (StringUtils.isEmpty(propertyW)) {
            return null;
        }
        String propertyH = read(KEY_WINDOWS_H);
        if (StringUtils.isEmpty(propertyH)) {
            return null;
        }
        return new Dimension(Integer.parseInt(propertyW), Integer.parseInt(propertyH));
    }

    public void setWindowsXY(int x, int y) {
        write(KEY_WINDOWS_X, String.valueOf(x));
        write(KEY_WINDOWS_Y, String.valueOf(y));
        persistent();
    }

    public void setWindowsSize(int w, int h) {
        write(KEY_WINDOWS_W, String.valueOf(w));
        write(KEY_WINDOWS_H, String.valueOf(h));
        persistent();
    }

    public boolean isFirstRun() {
        String property = read(KEY_IS_FIRST_RUN);
        if (StringUtils.isEmpty(property)) {
            return true;
        }
        return Boolean.parseBoolean(property);
    }

    public void setFirstRun(boolean flag) {
        write(KEY_IS_FIRST_RUN, String.valueOf(flag));
        persistent();
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

    public String getInitialFontSize() {
        String value = read(KEY_INITIAL_FONT_SIZE);
        if (StringUtils.isEmpty(value)) {
            String defaultFont = String.valueOf(UIManager.getFont("defaultFont").getSize());
            write(KEY_INITIAL_FONT_SIZE, defaultFont);
            return defaultFont;
        }
        return value;
    }

    public String getInitialFontFamily() {
        String fontFamily = read(KEY_INITIAL_FONT_FAMILY);
        if (StringUtils.isEmpty(fontFamily)) {
            String initialFontFamily = UIManager.getFont("Label.font").getFamily();
            write(KEY_INITIAL_FONT_FAMILY, initialFontFamily);
            return initialFontFamily;
        }
        return fontFamily;
    }

    public AppDataItem getFontSizeDataItem() {
        return new AppDataItem(AppData.getInstance(), KEY_FONT_SIZE);
    }

    public AppDataItem getFontFamilyDataItem() {
        return new AppDataItem(AppData.getInstance(), KEY_FONT_FAMILY);
    }

    public AppDataItem getThemeDataItem() {
        return new AppDataItem(AppData.getInstance(), KEY_FONT_THEME);
    }

    /**
     * 获取首选的字体大小：用户设置的->系统初始的
     *
     * @return 首选的字体大小
     */
    public String getPreferredFontSize() {
        return getFontSizeDataItem().getValue(getInitialFontSize());
    }

    public String getPreferredFontFamily() {
        return getFontFamilyDataItem().getValue(getInitialFontFamily());
    }


}
