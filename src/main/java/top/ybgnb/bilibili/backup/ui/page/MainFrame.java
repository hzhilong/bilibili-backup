package top.ybgnb.bilibili.backup.ui.page;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.ui.component.ComponentInit;
import top.ybgnb.bilibili.backup.ui.config.AppData;
import top.ybgnb.bilibili.backup.ui.config.AppProperties;
import top.ybgnb.bilibili.backup.ui.state.GlobalState;
import top.ybgnb.bilibili.backup.ui.utils.ResourceUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @ClassName MainFrame
 * @Description 主窗口
 * @Author hzhilong
 * @Time 2024/11/26
 * @Version 1.0
 */
@Slf4j
public class MainFrame extends JFrame implements ComponentInit {

    private AppProperties appProperties;
    private AppData appData;
    private final Dimension mixSize = new Dimension(900, 700);
    private Dimension defaultSize = new Dimension(900, 700);
    private Dimension defaultLocation;

    @Override
    public void initData() throws BusinessException {
        appProperties = AppProperties.getInstance();
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

    @Override
    public void initUI() throws BusinessException {
        setTitle(appProperties.getName() + " " + appProperties.getVersion());
        setIconImage(ResourceUtil.getImage("icon/app_logo_24.png"));
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

        MainTabbedPane mainTabbedPane = new MainTabbedPane();
        mainTabbedPane.setPreferredSize(defaultSize);
        mainTabbedPane.init();
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
    }
}
