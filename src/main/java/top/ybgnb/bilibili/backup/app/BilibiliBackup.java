package top.ybgnb.bilibili.backup.app;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.userInfoCallback.UserInfoCallback;
import top.ybgnb.bilibili.backup.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName BilibiliBackup
 * @Description bilibili备份工具
 * @Author hzhilong
 * @Time 2024/9/20
 * @Version 1.0
 */
@Slf4j
public class BilibiliBackup extends BaseApp {

    public static final String PATH_PREFIX = "backup-data/";

    public BilibiliBackup(List<ServiceBuilder> serviceBuilders, User user, UserInfoCallback userInfoCallback) {
        super(serviceBuilders, user, userInfoCallback);
    }

    @Override
    public Upper start() throws BusinessException {
        Upper upper = getUpper();
        String path = String.format("%s%s_%s_%s/", PATH_PREFIX, upper.getName(), user.getUid(),
                new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        for (ServiceBuilder serviceBuilder : this.serviceBuilders) {
            backup(serviceBuilder.build(client,user, path));
        }
        return upper;
    }

    public void backup(@NonNull BackupRestoreService... services) {
        for (BackupRestoreService service : services) {
            try {
                service.backup();
            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
            }
        }
    }


}
