package top.ybgnb.bilibili.backup.ui.config;

import com.formdev.flatlaf.FlatDarculaLaf;

/**
 * @ClassName FlatLafConfig
 * @Description FlatLaf 配置
 * @Author hzhilong
 * @Time 2024/11/27
 * @Version 1.0
 */
public class FlatLafConfig {

    public static void init(){
        // 启用 FlatLaf 外观库
        FlatDarculaLaf.setup();
        // 启用 FlatLaf 窗口装饰
        System.setProperty( "flatlaf.useWindowDecorations", "true" );
    }

}
