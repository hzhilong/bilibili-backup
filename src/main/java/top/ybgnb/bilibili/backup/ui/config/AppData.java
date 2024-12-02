package top.ybgnb.bilibili.backup.ui.config;

import top.ybgnb.bilibili.backup.biliapi.utils.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @ClassName AppData
 * @Description app配置
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
public class AppData extends Properties {

    private static final String CONFIG_PATH = "bin/app.data";

    public static final String KEY_IS_FIRST_RUN = "first.run";
    public static final String KEY_WINDOWS_X = "windows.x";
    public static final String KEY_WINDOWS_Y = "windows.y";
    public static final String KEY_WINDOWS_W = "windows.w";
    public static final String KEY_WINDOWS_H = "windows.h";

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

    private static class Holder {
        private final static AppData INSTANCE = new AppData();
    }

    public static AppData getInstance() {
        return AppData.Holder.INSTANCE;
    }

    public Dimension getWindowsLocation() {
        String propertyX = getProperty(KEY_WINDOWS_X);
        if (StringUtils.isEmpty(propertyX)) {
            return null;
        }
        String propertyY = getProperty(KEY_WINDOWS_Y);
        if (StringUtils.isEmpty(propertyY)) {
            return null;
        }
        return new Dimension(Integer.parseInt(propertyX), Integer.parseInt(propertyY));
    }

    public Dimension getWindowsSize() {
        String propertyW = getProperty(KEY_WINDOWS_W);
        if (StringUtils.isEmpty(propertyW)) {
            return null;
        }
        String propertyH = getProperty(KEY_WINDOWS_H);
        if (StringUtils.isEmpty(propertyH)) {
            return null;
        }
        return new Dimension(Integer.parseInt(propertyW), Integer.parseInt(propertyH));
    }

    public void setWindowsXY(int x, int y) {
        setProperty(KEY_WINDOWS_X, String.valueOf(x));
        setProperty(KEY_WINDOWS_Y, String.valueOf(y));
        saveData();
    }

    public void setWindowsSize(int w, int h) {
        setProperty(KEY_WINDOWS_W, String.valueOf(w));
        setProperty(KEY_WINDOWS_H, String.valueOf(h));
        saveData();
    }

    private void saveData() {
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(Paths.get(CONFIG_PATH)))) {
            store(writer, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFirstRun() {
        String property = getProperty(KEY_IS_FIRST_RUN);
        if (StringUtils.isEmpty(property)) {
            return true;
        }
        return Boolean.parseBoolean(property);
    }

    public void setFirstRun(boolean flag) {
        setProperty(KEY_IS_FIRST_RUN, String.valueOf(flag));
        saveData();
    }

    /**
     * 是否存在未完成的分段备份
     *
     * @param dirName  备份目录
     * @param itemName 备份项目名称
     * @return
     */
    public int getBackupSegment(String dirName, String itemName) {
        String property = getProperty(getBackupSegmentKey(dirName, itemName));
        if (StringUtils.isEmpty(property)) {
            return -1;
        }
        return Integer.parseInt(property);
    }

    public void setBackupSegment(String dirName, String itemName, int nextPageNum) {
        setProperty(getBackupSegmentKey(dirName, itemName), String.valueOf(nextPageNum));
        saveData();
    }

    private String getBackupSegmentKey(String dirName, String itemName) {
        return KEY_BACKUP_SEGMENT + dirName + "." + itemName;
    }
}
