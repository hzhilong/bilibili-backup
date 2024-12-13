package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.bilibili.backup.app.state.GlobalState;

import javax.swing.*;

/**
 * 延迟设置正处理的日志容器
 *
 * @author hzhilong
 * @version 1.0
 */
public class DelaySetProcessingLoggerRunnable implements Runnable {

    private final JTextArea processingLogger;

    public DelaySetProcessingLoggerRunnable(JTextArea processingLogger) {
        this.processingLogger = processingLogger;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
            GlobalState.setProcessingLogger(processingLogger);
        } catch (InterruptedException ignored) {

        }
    }
}
