package io.github.hzhilong.bilibili.backup.gui.component;

/**
 * 组件初始化（主动调用）
 *
 * @author hzhilong
 * @version 1.0
 */
public interface ComponentInit {

    default void init() {
        initData();
        initUI();
    }

    void initData();

    void initUI();

}
