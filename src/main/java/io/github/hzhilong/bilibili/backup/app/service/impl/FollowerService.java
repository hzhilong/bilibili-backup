package io.github.hzhilong.bilibili.backup.app.service.impl;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.Relation;
import io.github.hzhilong.bilibili.backup.api.bean.RelationAct;
import io.github.hzhilong.bilibili.backup.api.bean.page.PageData;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BusinessResult;
import io.github.hzhilong.bilibili.backup.app.service.RelationService;
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
public class FollowerService extends RelationService {
    PageApi<PageData<Relation>, Relation> pageApi;

    public FollowerService(OkHttpClient client, User user, String path) {
        super(client, user, path);
        this.pageApi = new PageApi<>(client, user, "https://api.bilibili.com/x/relation/fans",
                queryParams -> {
                    queryParams.put("vmid", user.getUid());
                    queryParams.put("order", "desc");
                }, Relation.class);
        this.pageApi.setPageSize(50);
    }

    @Override
    public List<BusinessResult<List<Relation>>> backup() throws BusinessException {
        return createResults(
                backupData("粉丝",
                        new BackupCallback<List<Relation>>() {
                            @Override
                            public List<Relation> getData() throws BusinessException {
                                return getFollowers();
                            }
                        }));
    }

    @Override
    public List<BusinessResult<List<Relation>>> restore() throws BusinessException {
        throw new BusinessException("粉丝数据仅可备份，不能还原 ( ﹁ ﹁ ) ~→");
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("粉丝", "Follower");
    }

    @Override
    public List<BusinessResult<List<Relation>>> clear() throws BusinessException {
        return createResults(clearList("粉丝", new ClearListCallback<Relation>() {
            @Override
            public List<Relation> getList() throws BusinessException {
                return FollowerService.this.getFollowers();
            }

            @Override
            public void delData(Relation data) throws BusinessException {
                modify(data, RelationAct.REMOVE_FOLLOWER, false);
            }

            @Override
            public String dataName(Relation data) {
                return String.format("粉丝[%s]", data.getUname());
            }
        }));
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "粉丝");
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        if (pageApi != null) {
            pageApi.setInterrupt(interrupt);
        }
        super.setInterrupt(interrupt);
    }

    public List<Relation> getFollowers() throws BusinessException {
        return pageApi.getAllData();
    }

    public void removeFollower(Relation follower) throws BusinessException {
        modify(follower, RelationAct.REMOVE_FOLLOWER, false);
    }
}
