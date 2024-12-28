package io.github.hzhilong.bilibili.backup.app.cli.business.impl;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.cli.business.BaseBusinessForLoginUser;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.cli.BackupRestoreMenu;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.bean.ServiceBuilder;
import io.github.hzhilong.bilibili.backup.api.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * 备份
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class BackupBusiness extends BaseBusinessForLoginUser {

    @Override
    public Upper process(Scanner scanner) throws BusinessException {
        // 1. 登录/选择账号
        SavedUser user = super.chooseUser(scanner);
        // 2. 选择操作项目
        List<ServiceBuilder> serviceItems = BackupRestoreMenu.chooseServiceItems(scanner);
        // 3. 设置当前备份目录
        String path = String.format("%s%s_%s_%s/", AppConstant.BACKUP_PATH_PREFIX, user.getName(), user.getMid(),
                new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        // 4. 执行各个项目
        for (ServiceBuilder item : serviceItems) {
            try {
                item.build(client, new User(user.getCookie()), path).backup();
            } catch (BusinessException ex) {
                log.info("操作失败，{}\n", ex.getMessage());
            }
        }
        return user;
    }

}
