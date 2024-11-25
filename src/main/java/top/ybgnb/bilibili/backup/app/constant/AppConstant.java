package top.ybgnb.bilibili.backup.app.constant;

import java.util.regex.Pattern;

public class AppConstant {

    /**
     * 备份路径前缀
     */
    public static final String BACKUP_PATH_PREFIX = "backup-data/";

    /**
     * cookie路径前缀
     */
    public static final String COOKIE_PATH_PREFIX = "bin/cookies/";

    /**
     * 纯数字
     */
    public static final Pattern NUM_PATTERN = Pattern.compile("[0-9]*");

    /**
     * 已注销账号的昵称
     */
    public static final String CANCELLED_ACCOUNT_NAME = "账号已注销";
}
