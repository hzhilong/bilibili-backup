package io.github.hzhilong.bilibili.backup.app.cli.btn;

import io.github.hzhilong.bilibili.backup.app.cli.btn.callback.BtnCallback;

/**
 * 按钮基类
 *
 * @author hzhilong
 * @version 1.0
 */
public abstract class BaseBtn<M extends BtnCallback> {

    /**
     * 显示按钮
     */
    public abstract void showBtn(M callback);

}
