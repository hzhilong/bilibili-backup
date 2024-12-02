package top.ybgnb.bilibili.backup.ui.config;

import top.ybgnb.bilibili.backup.ui.utils.ResourceUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @ClassName AppProperties
 * @Description app参数
 * @Author hzhilong
 * @Time 2024/11/27
 * @Version 1.0
 */
public class AppProperties extends Properties {

    private AppProperties() {
        try (InputStreamReader reader = new InputStreamReader(ResourceUtil.getResourceAsStream("app.properties"), "UTF-8")) {
            this.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Holder {
        private final static AppProperties INSTANCE = new AppProperties();
    }

    public static AppProperties getInstance() {
        return Holder.INSTANCE;
    }

    public String getName() {
        return getProperty("name");
    }

    public String getDescription() {
        return getProperty("description");
    }

    public String getVersion() {
        return getProperty("version");
    }

    public String getAuthor() {
        return getProperty("author");
    }

    public String getGithub() {
        return getProperty("github");
    }
}
