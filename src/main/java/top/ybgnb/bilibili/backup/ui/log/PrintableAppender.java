package top.ybgnb.bilibili.backup.ui.log;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;
import top.ybgnb.bilibili.backup.ui.state.GlobalState;

import javax.swing.*;

/**
 * @ClassName PrintableAppender
 * @Description
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
public class PrintableAppender extends ConsoleAppender {

    @Override
    public void append(LoggingEvent event) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTextArea processingLogger = GlobalState.getProcessingLogger();
                if (processingLogger != null) {
                    processingLogger.append(event.getMessage() + "\n");
                    processingLogger.setCaretPosition(processingLogger.getDocument().getLength());
                }
            }
        });
        super.append(event);
    }
}
