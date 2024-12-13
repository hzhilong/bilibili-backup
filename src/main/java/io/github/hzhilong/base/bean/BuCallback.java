package io.github.hzhilong.base.bean;

/**
 * 业务回调
 *
 * @author hzhilong
 * @version 1.0
 */
public interface BuCallback<D> {
    void success(D data);

    void fail(String msg);

    void interrupt();
}
