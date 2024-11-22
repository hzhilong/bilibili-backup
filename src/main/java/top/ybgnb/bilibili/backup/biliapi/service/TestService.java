package top.ybgnb.bilibili.backup.biliapi.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.user.User;

/**
 * @ClassName TestService
 * @Description 测试
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class TestService extends BackupRestoreService {

    public TestService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    @Override
    public void backup() throws BusinessException {
    }

    @Override
    public void restore() throws BusinessException {
    }
}
