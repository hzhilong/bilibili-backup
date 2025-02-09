package io.github.hzhilong.bilibili.backup.app.constant;

import java.util.regex.Pattern;

/**
 * 软件常量
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppConstant {

    /**
     * 应用图标
     */
    public static final String APP_ICON = "/icon/app_logo.svg";

    /**
     * 备份路径前缀
     */
    public static final String BACKUP_PATH_PREFIX = "backup-data/";
    /**
     * 备份路径前缀
     */
    public static final String BACKUP_OTHER_PATH_PREFIX = "backup-other/";

    /**
     * cookie路径前缀
     */
    public static final String COOKIE_PATH_PREFIX = "bin/cookies/";

    /**
     * 纯数字
     */
    public static final Pattern NUM_PATTERN = Pattern.compile("^[0-9]*$");

    /**
     * 已注销账号的昵称
     */
    public static final String CANCELLED_ACCOUNT_NAME = "账号已注销";

}
