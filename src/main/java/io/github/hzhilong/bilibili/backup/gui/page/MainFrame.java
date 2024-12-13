package io.github.hzhilong.bilibili.backup.gui.page;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.gui.component.menu.AppearanceMenu;
import io.github.hzhilong.bilibili.backup.gui.component.menu.HelpMenu;
import io.github.hzhilong.bilibili.backup.app.config.AppBuildProperties;
import io.github.hzhilong.bilibili.backup.gui.config.AppUIConfig;
import io.github.hzhilong.bilibili.backup.app.state.AppData;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;

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
                if (GlobalState.getProcessing()) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "正在操作，请勿退出！",
                            "提示", JOptionPane.WARNING_MESSAGE);
                } else {
                    AppData.getInstance().setWindowsXY(getLocation().x, getLocation().y);
                    AppData.getInstance().setWindowsSize(getSize().width, getSize().height);// 退出
                    System.exit(0);
                }
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

        if (appData.isFirstRun()) {
            JOptionPane.showMessageDialog(MainFrame.this,
                    "首次运行，若界面显示有问题，请手动调整窗口位置和大小",
                    "首次运行", JOptionPane.INFORMATION_MESSAGE);
            appData.setFirstRun(false);
        }
        setVisible(true);

        JMenuBar menuBar = new JMenuBar();
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
