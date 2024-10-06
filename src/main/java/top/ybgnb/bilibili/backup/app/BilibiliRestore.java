package top.ybgnb.bilibili.backup.app;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.bean.Upper;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.service.UserInfoCallback;
import top.ybgnb.bilibili.backup.user.User;

import java.util.List;

/**
 * @ClassName BilibiliRestore
 * @Description
 * @Author hzhilong
 * @Time 2024/9/25
 * @Version 1.0
 */
@Slf4j
public class BilibiliRestore extends BaseApp {

    private String oldPath;

    public BilibiliRestore(List<ServiceBuilder> serviceBuilders, String oldPath, User user, UserInfoCallback userInfoCallback) {
        super(serviceBuilders, user, userInfoCallback);
        this.oldPath = oldPath;
    }


    @Override
    public Upper start() throws BusinessException {
        Upper upper = getUpper();
        for (ServiceBuilder serviceBuilder : this.serviceBuilders) {
            restore(serviceBuilder.build(client, user, oldPath));
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
