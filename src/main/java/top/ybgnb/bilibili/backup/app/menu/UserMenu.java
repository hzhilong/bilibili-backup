package top.ybgnb.bilibili.backup.app.menu;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;
import top.ybgnb.bilibili.backup.app.state.UserManager;
import top.ybgnb.bilibili.backup.app.utils.MenuUtil;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;

import java.util.List;
import java.util.Scanner;

/**
 * @ClassName UserMenu
 * @Description 账号选择菜单
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
@Slf4j
public class UserMenu extends BaseMenu {

    /**
     * 选择账号
     *
     * @param scanner
     * @param users
     * @return
     */
    private static SavedUser chooseUser(Scanner scanner, List<SavedUser> users) {
        log.info("总共登录{}个账号，请输入前面的数字选择对应的账号", users.size());
        for (int i = 0; i < users.size(); i++) {
            log.info("[{}]-[{}]", i, users.get(i).getName());
        }
        int pos = MenuUtil.checkInputPos(users.size(), scanner.nextLine());
        return users.get(pos);
    }

    /**
     * 选择已登录的账号
     *
     * @return 已登录的账号
     */
    public static SavedUser chooseLoggedUser(Scanner scanner, boolean isTip) throws BusinessException {
        // 获取已经登录的账号
        List<SavedUser> users = UserManager.readAllUser();
        if (users == null) {
            return null;
        }
        if (isTip) {
            // 提示
            log.info("是否使用之前登录的账号？");
            log.info("输入Y：使用\t输入其他：不使用");
            String nextLine = scanner.nextLine();
            if ("Y".equals(nextLine) || "y".equals(nextLine)) {
                // 选择账号
                return chooseUser(scanner, users);
            }
        } else {
            // 选择账号
            return chooseUser(scanner, users);
        }
        return null;
    }

    public static String inputUid(Scanner sc) {
        log.info("请输入用户UID：");
        String nextLine = null;
        do {
            if (nextLine != null) {
                log.info("输入错误，请重新输入");
            }
            nextLine = sc.nextLine();
        } while (!AppConstant.NUM_PATTERN.matcher(nextLine).find());
        return nextLine;
    }
}
