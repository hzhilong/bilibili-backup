package top.ybgnb.bilibili.backup.app.bean;

import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;

/**
 * @ClassName UpperInfoCallback
 * @Description UP信息回调
 * @Author hzhilong
 * @Time 2024/9/29
 * @Version 1.0
 */
public interface UpperInfoCallback {
    void success(Upper upper) throws BusinessException;

    void fail(User user);
}
