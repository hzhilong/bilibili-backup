package top.ybgnb.bilibili.backup.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.bean.ApiResult;
import top.ybgnb.bilibili.backup.bean.FavCollected;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.request.ModifyApi;
import top.ybgnb.bilibili.backup.request.PageApi;
import top.ybgnb.bilibili.backup.user.User;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName FavCollected
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
        return new PageApi<>(client, user, "https://api.bilibili.com/x/v3/fav/folder/collected/list",
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
        restoreList("收藏的视频合集", FavCollected.class,new RestoreCallback<FavCollected>() {
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
}
