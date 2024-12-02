package top.ybgnb.bilibili.backup.app.business.impl;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.bean.SavedUser;
import top.ybgnb.bilibili.backup.app.business.BaseBusinessForLoginUser;
import top.ybgnb.bilibili.backup.app.menu.BackupRestoreMenu;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.util.List;
import java.util.Scanner;

/**
 * @ClassName RestoreBusiness
 * @Description 还原
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
@Slf4j
public class RestoreBusiness extends BaseBusinessForLoginUser {

    @Override
    public Upper process(Scanner scanner) throws BusinessException {
        // 1. 选择备份文件路径（父目录）
        String backupFilePath = BackupRestoreMenu.chooseBackupDir(scanner);
        // 2. 登录/选择账号
        SavedUser user = super.chooseUser(scanner);
        // 3. 选择操作项目
        List<ServiceBuilder> serviceItems = BackupRestoreMenu.chooseServiceItems(scanner);
        // 4. 执行各个项目
        for (ServiceBuilder item : serviceItems) {
            try {
                item.build(client, new User(user.getCookie()), backupFilePath).restore();
            } catch (BusinessException ex) {
                log.info("操作失败，{}\n", ex.getMessage());
            }
        }
        return user;
    }
}
