package io.github.hzhilong.bilibili.backup.gui.utils;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * 布局工具
 *
 * @author hzhilong
 * @version 1.0
 */
public class LayoutUtil {

    public static GridBagConstraints addGridBarX(Container container, Component component, int pos) {
        return addGridBar(container, component, pos, 0);
    }

    public static GridBagConstraints addGridBarY(Container container, Component component, int pos) {
        return addGridBar(container, component, 0, pos);
    }

    public static GridBagConstraints getSeparatorConstraints(int x, int y, int gridWidth) {
        GridBagConstraints constraints = getGridBagConstraints(x, y);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.gridwidth = gridWidth;
        return constraints;
    }

    public static JSeparator addSeparator(Container container, int x, int y, int gridWidth) {
        JSeparator separator = new JSeparator();
        container.add(separator, getSeparatorConstraints(x, y, gridWidth));
        return separator;
    }

    public static JSeparator addSeparator(Container container, int x, int y) {
        return addSeparator(container, x, y, 1);
    }

    public static GridBagConstraints addGridBar(Container container, Component component, int x, int y, int anchor) {
        GridBagConstraints constraints = getGridBagConstraints(x, y, anchor);
        container.add(component, constraints);
        return constraints;
    }

    public static GridBagConstraints addGridBar(Container container, Component component, int x, int y) {
        GridBagConstraints constraints = getGridBagConstraints(x, y);
        container.add(component, constraints);
        return constraints;
    }

    @NotNull
    public static GridBagConstraints getGridBagConstraints(int x, int y) {
        return getGridBagConstraints(x, y, GridBagConstraints.WEST);
    }

    public static GridBagConstraints getGridBagConstraints(int x, int y, int anchor) {
        // https://help.supermap.com/iDesktopX/1101/zh/tutorial/SpecialFeatures/Development/DevelopmentTutorial/UIControlAndLayout/GridBagLayout
        GridBagConstraints constraints = new GridBagConstraints();
        // 定位的列坐标
        constraints.gridx = x;
        // 定位的行坐标
        constraints.gridy = y;
        // 占用的宽度单位
        constraints.gridwidth = 1;
        // 占用的高度单位
        constraints.gridheight = 1;
        // 当指定容器大小改变时，可通过 weightx 和 weighty 设置组件之间宽度和高度变化的分配权重，
        // 即增加/减少的空间在组件间分配的权重，数值大表明组件所在的行或者列将获得更多的空间。
        // 默认值为 0，即所有的组件将聚拢在容器的中心，多余的空间将放在容器边缘与网格单元之间。
        constraints.weightx = 0;
        constraints.weighty = 0;
        // 组件在显示区域中的摆放位置
        constraints.anchor = anchor;
        // 组件填充网格的方式
        constraints.fill = GridBagConstraints.NONE;
        // 组件与其显示区域边缘之间的空间
        constraints.insets = new Insets(4, 4, 4, 4);
        return constraints;
    }
}
