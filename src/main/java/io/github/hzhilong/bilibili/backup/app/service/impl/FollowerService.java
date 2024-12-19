package io.github.hzhilong.bilibili.backup.app.service.impl;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.Relation;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BackupRestoreResult;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 粉丝
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class FollowerService extends BackupRestoreService<Relation> {

    public FollowerService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    @Override
    public List<BackupRestoreResult<List<Relation>>> backup() throws BusinessException {
        return createResults(
                backupData("粉丝",
                        () -> new PageApi<>(client, user, "https://api.bilibili.com/x/relation/followers",
                                queryParams -> {
                                    queryParams.put("vmid", user.getUid());
                                    queryParams.put("order", "desc");
                                }, Relation.class)
                                .getAllData()));
    }

    @Override
    public List<BackupRestoreResult<List<Relation>>> restore() throws BusinessException {
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
