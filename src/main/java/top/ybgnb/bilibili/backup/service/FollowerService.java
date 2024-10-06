package top.ybgnb.bilibili.backup.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.bean.Relation;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.request.PageApi;
import top.ybgnb.bilibili.backup.user.User;

/**
 * @ClassName FollowerService
 * @Description 粉丝
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class FollowerService extends BackupRestoreService {

    public FollowerService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    @Override
    public void backup() throws BusinessException {
        backupData("粉丝", () -> new PageApi<>(client, user, "https://api.bilibili.com/x/relation/followers",
                queryParams -> {
                    queryParams.put("vmid", user.getUid());
                    queryParams.put("order", "desc");
                },
                Relation.class).getAllData());
    }

    @Override
    public void restore() throws BusinessException {
        throw new BusinessException("粉丝数据仅可备份，不能还原 ( ﹁ ﹁ ) ~→");
    }
}
