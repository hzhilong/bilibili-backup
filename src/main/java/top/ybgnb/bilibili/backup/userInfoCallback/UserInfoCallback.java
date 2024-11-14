package top.ybgnb.bilibili.backup.userInfoCallback;

import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.user.User;

/**
 * @ClassName UserInfoCallback
 * @Description 用户信息回调
 * @Author hzhilong
 * @Time 2024/9/29
 * @Version 1.0
 */
public interface UserInfoCallback {
    void success(Upper currUser) throws BusinessException;

    void fail(User user);
}
