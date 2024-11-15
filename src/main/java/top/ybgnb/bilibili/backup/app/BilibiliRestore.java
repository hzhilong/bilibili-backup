package top.ybgnb.bilibili.backup.app;

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

import java.util.List;

import static top.ybgnb.bilibili.backup.utils.CommonUtil.okHttpClient;

/**
 * @ClassName BilibiliRestore
 * @Description
 * @Author hzhilong
 * @Time 2024/9/25
 * @Version 1.0
 */
@Slf4j
public class BilibiliRestore {

    public Upper restoreItemsBySelf(List<ServiceBuilder> serviceBuilders, String oldPath) throws BusinessException {
        User user = CommonUtil.currentUserThreadLocal.get();
        Upper upper = UserCountsUtil.getUpper(user);
        for (ServiceBuilder serviceBuilder : serviceBuilders) {
            restore(serviceBuilder.build(okHttpClient, user, oldPath));
        }
        return upper;
    }

    public void restore(@NonNull BackupRestoreService... services) {
        for (BackupRestoreService service : services) {
            try {
                service.restore();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
