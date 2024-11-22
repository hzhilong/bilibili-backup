package top.ybgnb.bilibili.backup.app.business.impl;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.business.BaseBusiness;
import top.ybgnb.bilibili.backup.app.menu.UserMenu;
import top.ybgnb.bilibili.backup.app.state.UserManager;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;

import java.util.Scanner;

/**
 * @ClassName UserManagerBusiness
 * @Description 用户管理
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
@Slf4j
public class UserManageBusiness extends BaseBusiness {

    @Override
    public Upper process(Scanner scanner) throws BusinessException {
        // 选择登录过的用户
        SavedUser savedUser = UserMenu.chooseLoggedUser(scanner, false);
        if (savedUser == null) {
            log.info("未登录过用户\n");
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
