package io.github.hzhilong.bilibili.backup.gui.dialog;

import io.github.hzhilong.bilibili.backup.app.state.appdata.AppDataItem;
import io.github.hzhilong.bilibili.backup.gui.component.AppCheckBoxSettingItem;
import io.github.hzhilong.bilibili.backup.gui.utils.LayoutUtil;

import javax.swing.*;
import java.awt.*;

/**
 * 设置
 *
 * @author hzhilong
 * @version 1.0
 */
public class SettingDialog extends BaseDialog {

    public SettingDialog(Window parent) {
        super(parent, "设置");
        initUI();
        setModal(true);
    }

    private void initUI() {
        // 对话框居于屏幕中央
        setLocationRelativeTo(null);
        // 点击对话框关闭按钮时，销毁对话框
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        int posY = 0;
        AppCheckBoxSettingItem allowFailure = new AppCheckBoxSettingItem(
                "[关注]还原失败时，继续还原下一个数据", AppDataItem.ALLOW_FAILURE);
        LayoutUtil.addGridBarY(contentPanel, allowFailure, posY++);
        AppCheckBoxSettingItem directRestore = new AppCheckBoxSettingItem(
                "还原时忽略新账号现有的数据，直接还原", AppDataItem.DIRECT_RESTORE);
        LayoutUtil.addGridBarY(contentPanel, directRestore, posY++);
        // 左上角显示
        add(contentPanel, new GridBagConstraints(0, 0, 1, 1,
                1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(20, 20, 20, 20), 0, 0));
    }

}
