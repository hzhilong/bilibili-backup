package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.Bangumi;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BackupRestoreResult;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 追番追剧
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class BangumiService extends BackupRestoreService<Bangumi> {

    public BangumiService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    public List<Bangumi> getList(String type) throws BusinessException {
        return new PageApi<>(client, signUser(),
                "https://api.bilibili.com/x/space/bangumi/follow/list",
                queryParams -> {
                    queryParams.put("vmid", user.getUid());
                    queryParams.put("type", type);
                    queryParams.put("follow_status", "0");
                }, Bangumi.class).getAllData();
    }

    private void addData(Bangumi data) throws BusinessException {
        ApiResult<JSONObject> apiResult = new ModifyApi<JSONObject>(client, user,
                "https://api.bilibili.com/pgc/web/follow/add", JSONObject.class)
                .modify(new HashMap<String, String>() {{
                    put("season_id", String.valueOf(data.getSeasonId()));
                }});
        if (apiResult.isFail()) {
            throw new BusinessException(apiResult);
        }
    }

    @Override
    public List<BackupRestoreResult<List<Bangumi>>> backup() throws BusinessException {
        return createResults(
                backupData("我的追番", () -> getList("1")),
                backupData("我的追剧", () -> getList("2")));
    }

    @Override
    public List<BackupRestoreResult<List<Bangumi>>> restore() throws BusinessException {
        return createResults(
                restoreList("我的追番", Bangumi.class, getRestoreCallback("1")),
                restoreList("我的追剧", Bangumi.class, getRestoreCallback("2")));
    }

    @NotNull
    private RestoreCallback<Bangumi> getRestoreCallback(String type) {
        return new RestoreCallback<Bangumi>() {
            @Override
            public List<Bangumi> getNewList() throws BusinessException {
                return getList(type);
            }

            @Override
            public String compareFlag(Bangumi data) {
                return String.valueOf(data.getSeasonId());
            }

            @Override
            public String dataName(Bangumi data) {
                return String.format("[%s]", data.getTitle());
            }

            @Override
            public void restoreData(Bangumi data) throws BusinessException {
                addData(data);
            }
        };
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("我的追番", "Bangumi1");
        fileNames.put("我的追剧", "Bangumi2");
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "我的追番")
                + getBackupListSize(dir, "", "我的追剧");
    }
}
