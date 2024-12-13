package io.github.hzhilong.bilibili.backup.gui.menu;

import com.formdev.flatlaf.FlatLaf;
import io.github.hzhilong.bilibili.backup.gui.config.ThemeConfig;
import io.github.hzhilong.bilibili.backup.app.state.appdata.AppDataItem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主题菜单
 *
 * @author hzhilong
 * @version 1.0
 */
public class ThemeMenu extends JMenu {

    private final Class<? extends FlatLaf> defaultTheme;

    private final AppDataItem<String> appDataItem;
    private String currThemeClassName;
    private final Class<? extends FlatLaf>[] themes;
    private final Map<String, String> themeClassMapName;
    private final Map<String, String> themeNameMapClass;
    private List<JCheckBoxMenuItem> themeMenuItems;

    public ThemeMenu(AppDataItem<String> appDataItem) {
        this(appDataItem, null);
    }

    public ThemeMenu(AppDataItem<String> appDataItem, Class<? extends FlatLaf>[] themes) {
        this.appDataItem = appDataItem;
        Class<? extends FlatLaf> appDefaultTheme = ThemeConfig.getDefaultTheme();
        this.currThemeClassName = appDataItem.getValue(appDefaultTheme.getName());
        this.themes = themes != null ? themes : ThemeConfig.getDefaultThemes();
        this.defaultTheme = appDefaultTheme;
        this.themeClassMapName = new HashMap<>();
        this.themeNameMapClass = new HashMap<>();
        init();
    }

    private void init() {
        setText("主题");

        JMenuItem resetMenuItem = new JMenuItem();
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrTheme(defaultTheme);
            }
        });
        add(resetMenuItem);
        addSeparator();

        themeMenuItems = new ArrayList<>(themes.length);
        ButtonGroup buttonGroup = new ButtonGroup();
        for (Class<? extends FlatLaf> theme : themes) {
            try {
                String className = theme.getName();
                FlatLaf flatLaf = theme.newInstance();
                String name = flatLaf.getName();
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);
                item.setSelected(className.equals(currThemeClassName));
                item.addItemListener(this::changed);
                buttonGroup.add(item);
                themeMenuItems.add(item);
                themeClassMapName.put(className, name);
                themeNameMapClass.put(name, className);
                add(item);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        resetMenuItem.setText("默认 " + themeClassMapName.get(defaultTheme.getName()));
    }

    private void changed(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getItem();
            currThemeClassName = themeNameMapClass.get(item.getText());
            updateAppUI();
            appDataItem.setValue(currThemeClassName);
        }
    }

    private void updateAppUI() {
        ThemeConfig.update(currThemeClassName);
    }

    public void setCurrTheme(Class<? extends FlatLaf> currTheme) {
        this.currThemeClassName = currTheme.getName();
        updateItemsState();
    }

    private void updateItemsState() {
        String currThemeName = themeClassMapName.get(currThemeClassName);
        for (JCheckBoxMenuItem menuItem : themeMenuItems) {
            menuItem.setSelected(menuItem.getText().equals(currThemeName));
        }
    }
}
