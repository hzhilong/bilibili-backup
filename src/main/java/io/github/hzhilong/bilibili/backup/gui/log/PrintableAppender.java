package io.github.hzhilong.bilibili.backup.gui.log;

import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;

/**
 * 自定义日志输出
 *
 * @author hzhilong
 * @version 1.0
 */
public class PrintableAppender extends ConsoleAppender {

    @Override
    public void append(LoggingEvent event) {
        SwingUtilities.invokeLater(() -> {
            JTextArea processingLogger = GlobalState.getProcessingLogger();
            if (processingLogger != null) {
                processingLogger.append(event.getMessage() + "\n");
                processingLogger.setCaretPosition(processingLogger.getDocument().getLength());
            }
        });
        super.append(event);
    }
}
