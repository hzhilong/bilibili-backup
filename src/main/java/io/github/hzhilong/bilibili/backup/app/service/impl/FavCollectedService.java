package io.github.hzhilong.bilibili.backup.app.service.impl;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.FavCollected;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BusinessResult;
import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏的视频合集
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class FavCollectedService extends BackupRestoreService<FavCollected> {

    public FavCollectedService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    private List<FavCollected> getList() throws BusinessException {
        return new PageApi<>(client, signUser(), "https://api.bilibili.com/x/v3/fav/folder/collected/list",
                queryParams -> {
                    queryParams.put("up_mid", user.getUid());
                    queryParams.put("platform", "web");
                },
                FavCollected.class).getAllData();
    }

    @Override
    public List<BusinessResult<List<FavCollected>>> backup() throws BusinessException {
        return createResults(backupData("收藏的视频合集", this::getList));
    }

    @Override
    public List<BusinessResult<List<FavCollected>>> restore() throws BusinessException {
        return createResults(restoreList("收藏的视频合集", FavCollected.class,
                new RestoreCallback<FavCollected>() {
                    @Override
                    public List<FavCollected> getNewList() throws BusinessException {
                        return getList();
                    }

                    @Override
                    public String compareFlag(FavCollected data) {
                        return String.valueOf(data.getId());
                    }

                    @Override
                    public String dataName(FavCollected data) {
                        return String.format("合集[%s]", data.getTitle());
                    }

                    @Override
                    public void restoreData(FavCollected data) throws BusinessException {
                        ApiResult<String> apiResult = new ModifyApi<String>(client, user,
                                "https://api.bilibili.com/x/v3/fav/season/fav", String.class)
                                .modify(
                                        new HashMap<String, String>() {{
                                            put("platform", "web");
                                            put("season_id", String.valueOf(data.getId()));
                                        }}
                                );
                        if (apiResult.isFail()) {
                            throw new ApiException(apiResult);
                        }
                    }
                }));
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("收藏的视频合集", "FavCollected");
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "收藏的视频合集");
    }

    @Override
    public List<BusinessResult<List<FavCollected>>> clear() throws BusinessException {
        return createResults(clearList("收藏的视频合集", new ClearListCallback<FavCollected>() {
            @Override
            public List<FavCollected> getList() throws BusinessException {
                return FavCollectedService.this.getList();
            }

            @Override
            public void delData(FavCollected data) throws BusinessException {
                ApiResult<Object> apiResult = new ModifyApi<Object>(client, user,
                        "https://api.bilibili.com/x/v3/fav/season/unfav", Object.class)
                        .modify(
                                new HashMap<String, String>() {{
                                    put("platform", "web");
                                    put("season_id", String.valueOf(data.getId()));
                                }}
                        );
                if (apiResult.isFail()) {
                    throw new ApiException(apiResult);
                }
            }

            @Override
            public String dataName(FavCollected data) {
                return String.format("合集[%s]", data.getTitle());
            }
        }));
    }
}
