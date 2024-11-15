package top.ybgnb.bilibili.backup.app;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.service.MessageService;
import top.ybgnb.bilibili.backup.user.User;
import top.ybgnb.bilibili.backup.utils.CommonUtil;
import top.ybgnb.bilibili.backup.utils.UserCountsUtil;

import static top.ybgnb.bilibili.backup.utils.CommonUtil.okHttpClient;

/**
 * @ClassName BilibiliRestore
 * @Description
 * @Author hzhilong
 * @Time 2024/9/25
 * @Version 1.0
 */
@Slf4j
public class BilibiliSessionMsg {
    
    public Upper readAllSession() throws BusinessException {
        User user = CommonUtil.currentUserThreadLocal.get();
        Upper upper = UserCountsUtil.getUpper(user);
        new MessageService(okHttpClient, user).readAllSession();
        return upper;
    }
}
