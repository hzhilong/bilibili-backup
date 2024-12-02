package top.ybgnb.bilibili.backup.ui.worker;

/**
 * @ClassName BuCallback
 * @Description 业务回调
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
public interface BuCallback<D> {
    void success(D data);

    void fail(String msg);

    void interrupt();
}
