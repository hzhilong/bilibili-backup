package top.ybgnb.bilibili.backup.app.business.impl;

import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.business.BaseBusinessForLoginUser;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.impl.MessageService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.util.Scanner;

/**
 * @ClassName ReadAllMessageBusiness
 * @Description 已读所有消息
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
public class ReadAllMessageBusiness extends BaseBusinessForLoginUser {

    @Override
    public Upper process(Scanner scanner) throws BusinessException {
        SavedUser user = super.chooseUser(scanner);
        new MessageService(client, new User(user.getCookie())).readAllSession();
        return user;
    }
}
