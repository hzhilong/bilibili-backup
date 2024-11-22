package top.ybgnb.bilibili.backup.biliapi.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.user.User;

/**
 * @ClassName BaseService
 * @Description
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Data
@Slf4j
public abstract class BaseService {

    protected OkHttpClient client;

    protected final User user;

    public BaseService(OkHttpClient client, User user) {
        this.client = client;
        this.user = user;
    }

}
