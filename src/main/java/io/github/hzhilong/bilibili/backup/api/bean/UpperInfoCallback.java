package io.github.hzhilong.bilibili.backup.api.bean;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.user.User;

/**
 * UP信息回调
 *
 * @author hzhilong
 * @version 1.0
 */
public interface UpperInfoCallback {
    void success(Upper upper) throws BusinessException;

    void fail(User user);
}
