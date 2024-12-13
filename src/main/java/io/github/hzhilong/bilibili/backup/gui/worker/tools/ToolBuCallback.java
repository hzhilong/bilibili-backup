package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.bean.BuCallback;

import javax.swing.*;

/**
 * 工具箱线程
 *
 * @author hzhilong
 * @version 1.0
 */
public abstract class ToolBuCallback<D> implements BuCallback<D> {

    protected JButton button;
    protected String name;

    public ToolBuCallback(JButton button, String name) {
        this.button = button;
        this.name = name;
    }

    public abstract void update(JButton button, String name, boolean busyStatus);

    @Override
    public void success(D data) {
        update(button, name, false);
    }

    @Override
    public void fail(String msg) {
        update(button, name, false);
    }

    @Override
    public void interrupt() {
        update(button, name, false);
    }
}