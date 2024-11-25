package top.ybgnb.bilibili.backup.app.menu.btn;

import top.ybgnb.bilibili.backup.app.menu.btn.callback.BtnCallback;

/**
 * @ClassName BaseBtn
 * @Description 按钮基类
 * @Author hzhilong
 * @Time 2024/11/25
 * @Version 1.0
 */
public abstract class BaseBtn<M extends BtnCallback> {

    /**
     * 显示按钮
     *
     * @param callback
     */
    public abstract void showBtn(M callback);

}
