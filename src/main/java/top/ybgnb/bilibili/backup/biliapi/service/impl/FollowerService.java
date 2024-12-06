package top.ybgnb.bilibili.backup.biliapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.Relation;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.io.File;
import java.util.Map;

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

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("粉丝", "Follower");
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "粉丝");
    }
}
