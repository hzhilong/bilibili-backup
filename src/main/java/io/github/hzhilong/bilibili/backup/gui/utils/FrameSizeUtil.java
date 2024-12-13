package io.github.hzhilong.bilibili.backup.gui.utils;


import java.awt.*;

/**
 * 窗口Size工具
 *
 * @author hzhilong
 * @version 1.0
 */
public class FrameSizeUtil {

    public static Toolkit DEFAULT_TOOLKIT = Toolkit.getDefaultToolkit();
    /**
     * 屏幕大小
     */
    public static Dimension SCREEN_SIZE = DEFAULT_TOOLKIT.getScreenSize();
    /**
     * 屏幕边界
     */
    public static Insets SCREEN_INSETS = DEFAULT_TOOLKIT.getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
    /**
     * 可用的宽度
     */
    public static int AVAILABLE_WIDTH = SCREEN_SIZE.width - SCREEN_INSETS.left - SCREEN_INSETS.right;
    /**
     * 可用的高度
     */
    public static int AVAILABLE_HEIGHT = SCREEN_SIZE.height - SCREEN_INSETS.top - SCREEN_INSETS.bottom;


}
