package io.github.hzhilong.bilibili.backup.app.business.impl;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.business.BaseBusinessForLoginUser;
import io.github.hzhilong.bilibili.backup.app.cli.BackupRestoreMenu;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.bean.ServiceBuilder;
import io.github.hzhilong.bilibili.backup.api.user.User;

import java.util.List;
import java.util.Scanner;

/**
 * 还原
 *
 * @author hzhilong
 * @version 1.0
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
