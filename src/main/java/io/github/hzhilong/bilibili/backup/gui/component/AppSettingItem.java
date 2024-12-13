package io.github.hzhilong.bilibili.backup.gui.component;

import io.github.hzhilong.bilibili.backup.app.state.appdata.AppDataItem;

import javax.swing.*;
import java.awt.*;

/**
 * 应用设置项
 *
 * @author hzhilong
 * @version 1.0
 */
public abstract class AppSettingItem<D> extends JPanel implements ComponentInit {

    /**
     * 描述文本
     */
    private String text;

    /**
     * 绑定的数据
     */
    private D data;

    /**
     * 应用序列化的数据项
     */
    private AppDataItem<D> appDataItem;

    public AppSettingItem(String text, AppDataItem<D> appDataItem) {
        this.text = text;
        this.appDataItem = appDataItem;
        init();
    }

    @Override
    public void initData() {
    }

    @Override
    public void initUI() {
        setLayout(new GridBagLayout());
        initContentUI(this, text, appDataItem.getValue());
    }

    public abstract void initContentUI(JPanel contentPanel, String text, D initialValue);

    public void updateAppDataItem(D newData) {
        appDataItem.setValue(newData);
    }
}
