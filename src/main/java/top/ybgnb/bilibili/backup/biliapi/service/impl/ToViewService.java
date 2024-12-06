package top.ybgnb.bilibili.backup.biliapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.Video;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.ListApi;
import top.ybgnb.bilibili.backup.biliapi.request.ModifyApi;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ToViewService
 * @Description 稍后再看
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class ToViewService extends BackupRestoreService {

    public ToViewService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    @Override
    public void backup() throws BusinessException {
        backupData("稍后再看", this::getList);
    }

    private List<Video> getList() throws BusinessException {
        return new ListApi<>(client, user, "https://api.bilibili.com/x/v2/history/toview/web",
                Video.class).getList();
    }

    @Override
    public void restore() throws BusinessException {
        restoreList("稍后再看", Video.class, new RestoreCallback<Video>() {
            @Override
            public List<Video> getNewList() throws BusinessException {
                return getList();
            }

            @Override
            public String compareFlag(Video data) {
                return data.getBvid();
            }

            @Override
            public String dataName(Video data) {
                return String.format("视频[%s]", data.getTitle());
            }

            @Override
            public void restoreData(Video data) throws BusinessException {
                ApiResult<Void> apiResult = new ModifyApi<Void>(client, user,
                        "https://api.bilibili.com/x/v2/history/toview/add", Void.class).modify(
                        new HashMap<String, String>() {{
                            put("aid", String.valueOf(data.getAid()));
                        }}
                );
                if (apiResult._isFail()) {
                    throw new BusinessException(apiResult);
                }
            }
        });
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("稍后再看", "ToView");
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "稍后再看");
    }
}
