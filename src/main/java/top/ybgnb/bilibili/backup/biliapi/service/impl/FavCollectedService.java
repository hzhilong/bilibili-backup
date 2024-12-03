package top.ybgnb.bilibili.backup.biliapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.FavCollected;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.ModifyApi;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName FavCollectedService
 * @Description 收藏的视频合集
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class FavCollectedService extends BackupRestoreService {

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
    public void backup() throws BusinessException {
        backupData("收藏的视频合集", this::getList);
    }

    @Override
    public void restore() throws BusinessException {
        restoreList("收藏的视频合集", FavCollected.class, new RestoreCallback<FavCollected>() {
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
                        "https://api.bilibili.com/x/v3/fav/season/fav", String.class).modify(
                        new HashMap<String, String>() {{
                            put("platform", "web");
                            put("season_id", String.valueOf(data.getId()));
                        }}
                );
                if (apiResult._isFail()) {
                    throw new BusinessException(apiResult);
                }
            }
        });
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "收藏的视频合集");
    }
}
