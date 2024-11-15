package top.ybgnb.bilibili.backup.service.impl;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.BilibiliSessionMsg;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.service.BaseBusinessService;
import top.ybgnb.bilibili.backup.utils.UserCountsUtil;


/**
 * @author Dream
 */
@Slf4j
public class ReadAllMsgBusinessService implements BaseBusinessService {

    @Override
    public Upper process(Object requestMsg) throws BusinessException {
        UserCountsUtil.getCookie();
        return new BilibiliSessionMsg().readAllSession();
    }

    @Override
    public BuType getRequestType() {
        return BuType.READ_ALL_MSG;
    }
}
