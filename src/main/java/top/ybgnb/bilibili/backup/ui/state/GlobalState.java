package top.ybgnb.bilibili.backup.ui.state;

import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.request.ThrottlingInterceptor;

import javax.swing.*;

/**
 * @ClassName GlobalState
 * @Description 全局状态
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
public class GlobalState {
    public static OkHttpClient CLIENT = new OkHttpClient.Builder().addInterceptor(
            new ThrottlingInterceptor(1000)).build();

    /**
     * 处理中（禁止退出/切换tab）
     */
    private static boolean processing = false;
    private static JTextArea processingLogger = null;

    public static synchronized boolean getProcessing(){
        return processing;
    }

    public static synchronized void setProcessing(boolean flag){
        processing = flag;
    }

    public static synchronized JTextArea getProcessingLogger(){
        return processingLogger;
    }

    public static synchronized void setProcessingLogger(JTextArea jTextArea){
        processingLogger = jTextArea;
    }

}
