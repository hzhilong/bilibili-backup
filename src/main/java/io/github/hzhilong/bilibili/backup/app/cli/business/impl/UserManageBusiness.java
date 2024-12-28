package io.github.hzhilong.bilibili.backup.app.cli.business.impl;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.cli.business.BaseBusiness;
import io.github.hzhilong.bilibili.backup.app.cli.UserMenu;
import io.github.hzhilong.bilibili.backup.app.state.UserManager;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.base.error.BusinessException;

import java.util.Scanner;

/**
 * 账号管理
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class UserManageBusiness extends BaseBusiness {

    @Override
    public Upper process(Scanner scanner) throws BusinessException {
        // 选择登录过的账号
        SavedUser savedUser = UserMenu.chooseLoggedUser(scanner, false);
        if (savedUser == null) {
            log.info("未登录过账号\n");
            return null;
        }
        log.info("输入D：删除\t输入其他：返回");
        String nextLine = scanner.nextLine();
        if ("D".equals(nextLine) || "d".equals(nextLine)) {
            UserManager.delete(String.valueOf(savedUser.getMid()));
            return savedUser;
        }
        return null;
    }
}
