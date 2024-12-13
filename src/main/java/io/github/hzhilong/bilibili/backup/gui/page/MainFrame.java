package io.github.hzhilong.bilibili.backup.gui.page;

import io.github.hzhilong.bilibili.backup.App;
import io.github.hzhilong.bilibili.backup.app.config.AppBuildProperties;
import io.github.hzhilong.bilibili.backup.app.state.appdata.AppData;
import io.github.hzhilong.bilibili.backup.app.state.appdata.AppDataItem;
import io.github.hzhilong.bilibili.backup.gui.config.AppUIConfig;
import io.github.hzhilong.bilibili.backup.gui.menu.AppMenu;
import io.github.hzhilong.bilibili.backup.gui.menu.AppearanceMenu;
import io.github.hzhilong.bilibili.backup.gui.menu.HelpMenu;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 主窗口
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class MainFrame extends JFrame {

    private AppBuildProperties appProperties;
    private AppData appData;
    private final Dimension mixSize = new Dimension(1010, 700);
    private Dimension defaultSize = new Dimension(1010, 700);
    private Dimension defaultLocation;

    public MainFrame() {
        initData();
        initUI();
    }

    public void initData() {
        appProperties = AppBuildProperties.getInstance();
        appData = AppData.getInstance();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                App.exitApp();
            }
        });
        // 获取上一次窗口的坐标和大小
        defaultLocation = appData.getWindowsLocation();
        Dimension windowsSize = appData.getWindowsSize();
        if (windowsSize != null) {
            defaultSize = windowsSize;
        }
    }

    public void initUI() {
        setTitle(appProperties.getName() + " " + appProperties.getVersion());
        AppUIConfig.initAppIcon(this);
        setLayout(new GridBagLayout());
        // 还原上一次窗口的坐标和大小
        setSize((int) defaultSize.getWidth(), (int) defaultSize.getHeight());
        setMinimumSize(mixSize);
        if (defaultLocation == null) {
            // 居中
            setLocationRelativeTo(null);
        } else {
            setLocation(defaultLocation.width, defaultLocation.height);
        }

        initLoadingDialog();

        MainTabbedPane mainTabbedPane = new MainTabbedPane();
        mainTabbedPane.setPreferredSize(defaultSize);
        add(mainTabbedPane, new GridBagConstraints(0, 0, 1, 1,
                1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        Boolean isFirstRun = AppDataItem.IS_FIRST_RUN.getValue();
        if (isFirstRun == null || isFirstRun) {
            JOptionPane.showMessageDialog(MainFrame.this,
                    "首次运行，若界面显示有问题，请手动调整窗口位置和大小",
                    "首次运行", JOptionPane.INFORMATION_MESSAGE);
            AppDataItem.IS_FIRST_RUN.setValue(false);
        }
        setVisible(true);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new AppMenu());
        menuBar.add(new AppearanceMenu());
        menuBar.add(new HelpMenu());
        this.setJMenuBar(menuBar);
    }

    private void initLoadingDialog() {
        /*LoadingDialog loadingDialog = new LoadingDialog("启动中，请稍候……");
        loadingDialog.showDialog();
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {

            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {
                loadingDialog.closeDialog(1000);
            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });*/
    }

}
