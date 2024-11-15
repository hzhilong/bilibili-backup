package top.ybgnb.bilibili.backup.app;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.userInfoCallback.UserInfoCallback;
import top.ybgnb.bilibili.backup.user.User;
import top.ybgnb.bilibili.backup.utils.CommonUtil;
import top.ybgnb.bilibili.backup.utils.UserCountsUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static top.ybgnb.bilibili.backup.utils.CommonUtil.okHttpClient;

/**
 * @ClassName BilibiliBackup
 * @Description bilibili备份工具
 * @Author hzhilong
 * @Time 2024/9/20
 * @Version 1.0
 */
@Slf4j
@NoArgsConstructor
public class BilibiliBackup  {
    
    public static final String PATH_PREFIX = "backup-data/";
    
    public Upper BackupItemsBySelf(List<ServiceBuilder> serviceBuilders) throws BusinessException {
        User user = CommonUtil.currentUserThreadLocal.get();
        Upper upper = UserCountsUtil.getUpper(user);
        
        String path = String.format("%s%s_%s_%s/", PATH_PREFIX, upper.getName(), user.getUid(),
                new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        for (ServiceBuilder serviceBuilder : serviceBuilders) {
            backup(serviceBuilder.build(okHttpClient,user, path));
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
