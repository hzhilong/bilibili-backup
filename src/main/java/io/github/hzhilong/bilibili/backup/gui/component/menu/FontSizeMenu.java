package io.github.hzhilong.bilibili.backup.gui.component.menu;

import io.github.hzhilong.bilibili.backup.gui.config.FontConfig;
import io.github.hzhilong.bilibili.backup.app.state.AppDataItem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 字体大小菜单
 *
 * @author hzhilong
 * @version 1.0
 */
public class FontSizeMenu extends JMenu {

    public static final String[] DEFAULT_FONT_SIZES = {"10", "12", "13", "14", "15", "16", "18", "20", "22", "24", "26"};

    private final AppDataItem appDataItem;
    private String currFontSize;
    private final String[] fontSizes;

    /**
     * 初始的字体大小
     */
    private final String initialFontSize;
    private List<JCheckBoxMenuItem> fontSizeMenuItems;

    public FontSizeMenu(AppDataItem appDataItem, String initialFontSize) {
        this(appDataItem, null, initialFontSize);
    }

    public FontSizeMenu(AppDataItem appDataItem, String[] fontSizes, String initialFontSize) {
        this.appDataItem = appDataItem;
        this.fontSizes = fontSizes == null ? DEFAULT_FONT_SIZES : fontSizes;
        this.initialFontSize = initialFontSize;
        this.currFontSize = appDataItem.getValue(initialFontSize);
        init();
    }

    private void init() {
        setText("字体大小");

        JMenuItem resetMenuItem = new JMenuItem("默认 " + initialFontSize);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrFontSize(initialFontSize);
            }
        });
        add(resetMenuItem);
        addSeparator();

        fontSizeMenuItems = new ArrayList<>(fontSizes.length);
        ButtonGroup buttonGroup = new ButtonGroup();
        for (String fontSize : fontSizes) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(fontSize);
            item.setSelected(fontSize.equals(String.valueOf(currFontSize)));
            item.addItemListener(this::changed);
            buttonGroup.add(item);
            fontSizeMenuItems.add(item);
            add(item);
        }
    }

    public void changed(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getItem();
            currFontSize = item.getText();
            updateAppUI();
            appDataItem.setValue(currFontSize);
        }
    }

    private void updateAppUI() {
        FontConfig.update(Integer.parseInt(currFontSize));
    }

    public void setCurrFontSize(String currFontSize) {
        this.currFontSize = currFontSize;
        updateItemsState();
    }

    private void updateItemsState() {
        for (JCheckBoxMenuItem menuItem : fontSizeMenuItems) {
            menuItem.setSelected(menuItem.getText().equals(currFontSize));
        }
    }
}
