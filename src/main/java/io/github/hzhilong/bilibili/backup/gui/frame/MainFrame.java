package io.github.hzhilong.bilibili.backup.gui.frame;

import io.github.hzhilong.baseapp.component.BaseMainFrame;
import io.github.hzhilong.baseapp.menu.BaseAppearanceMenu;
import io.github.hzhilong.baseapp.menu.BaseHelpMenu;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.gui.menu.AppMenu;
import io.github.hzhilong.bilibili.backup.gui.component.MainTabbedPane;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class MainFrame extends BaseMainFrame {

    public MainFrame() {
        super(AppConstant.APP_ICON, new Dimension(1010, 700));
        init();
    }

    public void init() {
        MainTabbedPane mainTabbedPane = new MainTabbedPane(this);
        setLayout(new GridBagLayout());
        add(mainTabbedPane, new GridBagConstraints(0, 0, 1, 1,
                1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new AppMenu(this, appIconPath));
        menuBar.add(new BaseAppearanceMenu(this, appIconPath));
        menuBar.add(new BaseHelpMenu(this, appIconPath));
        this.setJMenuBar(menuBar);
    }

    @Override
    public void dispose() {
        if (GlobalState.getProcessing()) {
            JOptionPane.showMessageDialog(this,
                    "正在操作，请勿退出！", "提示", JOptionPane.WARNING_MESSAGE);
        } else {
            super.dispose();
        }
    }
}
