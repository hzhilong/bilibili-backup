package top.ybgnb.bilibili.backup.utils;

import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.request.ThrottlingInterceptor;
import top.ybgnb.bilibili.backup.user.User;

import java.util.Scanner;

/**
 * @author Dream
 */
public class CommonUtil {
    
    public static ThreadLocal<User> currentUserThreadLocal = new ThreadLocal<>();

    public static ThreadLocal<BuType> buTypeThreadLocal = new ThreadLocal<>();

    public static ThreadLocal<Scanner> scannerThreadLocal = new ThreadLocal<>();
    
    public static OkHttpClient okHttpClient = 
            new OkHttpClient.Builder().addInterceptor(new ThrottlingInterceptor(1000)).build();
}
