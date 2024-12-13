package io.github.hzhilong.bilibili.backup.gui.menu;

import io.github.hzhilong.bilibili.backup.gui.config.FontConfig;
import io.github.hzhilong.bilibili.backup.app.state.appdata.AppDataItem;
import io.github.hzhilong.bilibili.backup.gui.utils.SystemUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 字体菜单
 *
 * @author hzhilong
 * @version 1.0
 */
public class FontFamilyMenu extends JMenu {

    private final AppDataItem<String> appDataItem;
    private String currFontFamily;
    private final String initialFontFamily;
    private List<JCheckBoxMenuItem> fontFamilyMenuItems;

    public FontFamilyMenu(AppDataItem<String> appDataItem, String initialFontFamily) {
        this.appDataItem = appDataItem;
        this.initialFontFamily = initialFontFamily;
        this.currFontFamily = appDataItem.getValue(initialFontFamily);
        init();
    }

    private void init() {
        setText("字体");

        JMenuItem resetMenuItem = new JMenuItem("默认 " + initialFontFamily);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrFontFamily(initialFontFamily);
            }
        });
        add(resetMenuItem);
        addSeparator();

        LinkedHashSet<String> cnFontNames = SystemUtil.getCNFontNames();
        fontFamilyMenuItems = new ArrayList<>(cnFontNames.size());
        ButtonGroup buttonGroup = new ButtonGroup();
        for (String cnFontName : cnFontNames) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(cnFontName);
            item.setSelected(cnFontName.equals(String.valueOf(currFontFamily)));
            item.addItemListener(this::changed);
            buttonGroup.add(item);
            fontFamilyMenuItems.add(item);
            add(item);
        }
    }

    private void changed(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getItem();
            currFontFamily = item.getText();
            updateAppUI();
            appDataItem.setValue(currFontFamily);
        }
    }

    private void updateAppUI() {
        FontConfig.update(currFontFamily);
    }

    public void setCurrFontFamily(String currFontFamily) {
        this.currFontFamily = currFontFamily;
        updateItemsState();
    }

    private void updateItemsState() {
        for (JCheckBoxMenuItem menuItem : fontFamilyMenuItems) {
            menuItem.setSelected(menuItem.getText().equals(currFontFamily));
        }
    }
}
