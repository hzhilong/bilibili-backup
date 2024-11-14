package top.ybgnb.bilibili.backup.userInfoCallback;

import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.user.User;
import top.ybgnb.bilibili.backup.utils.CommonUtil;
import top.ybgnb.bilibili.backup.utils.UserCountsUtil;

import static top.ybgnb.bilibili.backup.utils.CommonUtil.buTypeThreadLocal;

/**
 * @author Dream
 */
public class DefaultUserInfoCallback implements UserInfoCallback {
    @Override
    public void success(Upper currUser) throws BusinessException {
        UserCountsUtil.save(new UserCountsUtil.Cookie(buTypeThreadLocal.get(), currUser, CommonUtil.userCookieThreadLocal.get()));
    }

    @Override
    public void fail(User user) {
        UserCountsUtil.delete(buTypeThreadLocal.get());
    }
}
