package top.ybgnb.bilibili.backup.app;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.service.MessageService;
import top.ybgnb.bilibili.backup.service.UserInfoCallback;
import top.ybgnb.bilibili.backup.user.User;

/**
 * @ClassName BilibiliRestore
 * @Description
 * @Author hzhilong
 * @Time 2024/9/25
 * @Version 1.0
 */
@Slf4j
public class BilibiliReadAllMsg extends BaseApp {

    public BilibiliReadAllMsg(User user, UserInfoCallback userInfoCallback) {
        super(null, user, userInfoCallback);
    }

    @Override
    public Upper start() throws BusinessException {
        Upper upper = getUpper();
        new MessageService(client, user).readAllSession();
        return upper;
    }
}
