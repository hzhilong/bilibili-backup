package io.github.hzhilong.bilibili.backup.app.business.impl;

import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.business.BaseBusinessForLoginUser;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.service.impl.MessageService;
import io.github.hzhilong.bilibili.backup.api.user.User;

import java.util.Scanner;

/**
 * 已读所有消息
 *
 * @author hzhilong
 * @version 1.0
 */
public class ReadAllMessageBusiness extends BaseBusinessForLoginUser {

    @Override
    public Upper process(Scanner scanner) throws BusinessException {
        SavedUser user = super.chooseUser(scanner);
        new MessageService(client, new User(user.getCookie())).readAllSession();
        return user;
    }
}
