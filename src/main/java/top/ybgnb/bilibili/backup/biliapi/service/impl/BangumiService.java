package top.ybgnb.bilibili.backup.biliapi.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.Bangumi;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.ModifyApi;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName BangumiService
 * @Description 追番追剧
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class BangumiService extends BackupRestoreService {

    public BangumiService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    public List<Bangumi> getList(String type) throws BusinessException {
        return new PageApi<>(client, signUser(), "https://api.bilibili.com/x/space/bangumi/follow/list",
                queryParams -> {
                    queryParams.put("vmid", user.getUid());
                    queryParams.put("type", type);
                    queryParams.put("follow_status", "0");
                }, Bangumi.class).getAllData();
    }

    private void addData(Bangumi data) throws BusinessException {
        ApiResult<JSONObject> apiResult = new ModifyApi<JSONObject>(client, user, "https://api.bilibili.com/pgc/web/follow/add", JSONObject.class)
                .modify(new HashMap<String, String>() {{
                    put("season_id", String.valueOf(data.getSeasonId()));
                }});
        if (apiResult._isFail()) {
            throw new BusinessException(apiResult);
        }
    }

    @Override
    public void backup() throws BusinessException {
        backupData("我的追番", () -> getList("1"));
        backupData("我的追剧", () -> getList("2"));
    }

    @Override
    public void restore() throws BusinessException {
        restoreList("我的追番", Bangumi.class, new RestoreCallback<Bangumi>() {
            @Override
            public List<Bangumi> getNewList() throws BusinessException {
                return getList("1");
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
        });

        restoreList("我的追剧", Bangumi.class, new RestoreCallback<Bangumi>() {
            @Override
            public List<Bangumi> getNewList() throws BusinessException {
                return getList("2");
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
        });
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "我的追番")
                + getBackupListSize(dir, "", "我的追剧");
    }
}
