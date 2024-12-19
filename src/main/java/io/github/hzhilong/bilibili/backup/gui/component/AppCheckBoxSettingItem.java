package io.github.hzhilong.bilibili.backup.gui.component;

import io.github.hzhilong.bilibili.backup.app.state.appdata.AppDataItem;
import io.github.hzhilong.bilibili.backup.gui.utils.LayoutUtil;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * 应用复选框设置项
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppCheckBoxSettingItem extends AppSettingItem<Boolean> {

    public AppCheckBoxSettingItem(String text, AppDataItem<Boolean> appDataItem) {
        super(text, appDataItem);
    }

    @Override
    public void initContentUI(JPanel contentPanel, String text, Boolean initialValue) {
        JCheckBox jCheckBox = new JCheckBox(text);
        jCheckBox.setSelected(initialValue);
        LayoutUtil.addGridBar(contentPanel, jCheckBox, 0, 0);
        jCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateAppDataItem(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
    }
}
