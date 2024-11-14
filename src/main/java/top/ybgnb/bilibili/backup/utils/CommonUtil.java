package top.ybgnb.bilibili.backup.utils;

import top.ybgnb.bilibili.backup.constant.BuType;

import java.util.Scanner;

/**
 * @author Dream
 */
public class CommonUtil {
    
    public static ThreadLocal<String> userCookieThreadLocal = new ThreadLocal<>();

    public static ThreadLocal<BuType> buTypeThreadLocal = new ThreadLocal<>();

    public static ThreadLocal<Scanner> scannerThreadLocal = new ThreadLocal<>();
}
