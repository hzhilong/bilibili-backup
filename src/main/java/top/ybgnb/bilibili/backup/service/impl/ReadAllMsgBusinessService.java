package top.ybgnb.bilibili.backup.service.impl;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.BilibiliReadAllMsg;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.service.BaseBusinessService;
import top.ybgnb.bilibili.backup.user.User;
import top.ybgnb.bilibili.backup.userInfoCallback.DefaultUserInfoCallback;
import top.ybgnb.bilibili.backup.utils.UserCountsUtil;

import static top.ybgnb.bilibili.backup.utils.CommonUtil.buTypeThreadLocal;
import static top.ybgnb.bilibili.backup.utils.CommonUtil.userCookieThreadLocal;

/**
 * @author Dream
 */
@Slf4j
public class ReadAllMsgBusinessService implements BaseBusinessService {
    @Override
    public Object process(Object requestMsg) throws BusinessException {
        UserCountsUtil.getCookie();
        Upper upper = new BilibiliReadAllMsg(new User(userCookieThreadLocal.get()), new DefaultUserInfoCallback()).start();
        log.info(String.format("成功%s[%s]", buTypeThreadLocal.get().getCnName(), upper.getName()));
        return null;
    }

    @Override
    public BuType getRequestType() {
        return BuType.READ_ALL_MSG;
    }
}
