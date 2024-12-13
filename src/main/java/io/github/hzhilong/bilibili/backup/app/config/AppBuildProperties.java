package io.github.hzhilong.bilibili.backup.app.config;

import io.github.hzhilong.bilibili.backup.app.utils.ResourceUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * app构建参数
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppBuildProperties extends Properties {

    private AppBuildProperties() {
        try (InputStreamReader reader = new InputStreamReader(ResourceUtil.getResourceAsStream("app.properties"), StandardCharsets.UTF_8)) {
            this.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Holder {
        private final static AppBuildProperties INSTANCE = new AppBuildProperties();
    }

    public static AppBuildProperties getInstance() {
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
