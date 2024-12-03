package top.ybgnb.bilibili.backup.ui.worker;

import top.ybgnb.bilibili.backup.ui.state.GlobalState;

import javax.swing.*;

/**
 * @ClassName DelaySetProcessingLoggerRunnable
 * @Description 延迟设置正处理的日志容器
 * @Author hzhilong
 * @Time 2024/11/29
 * @Version 1.0
 */
public class DelaySetProcessingLoggerRunnable implements Runnable {

    private JTextArea processingLogger;

    public DelaySetProcessingLoggerRunnable(JTextArea processingLogger) {
        this.processingLogger = processingLogger;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
            GlobalState.setProcessingLogger(processingLogger);
        } catch (InterruptedException e) {

        }
    }
}
