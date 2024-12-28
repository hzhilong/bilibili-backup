package io.github.hzhilong.bilibili.backup.app.service.impl;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.Video;
import io.github.hzhilong.bilibili.backup.api.request.ListApi;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BusinessResult;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 稍后再看
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class ToViewService extends BackupRestoreService<Video> {

    public ToViewService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    private List<Video> getList() throws BusinessException {
        return new ListApi<>(client, user, "https://api.bilibili.com/x/v2/history/toview/web",
                Video.class).getList();
    }

    @Override
    public List<BusinessResult<List<Video>>> backup() throws BusinessException {
        return createResults(backupData("稍后再看", this::getList));
    }

    @Override
    public List<BusinessResult<List<Video>>> restore() throws BusinessException {
        return createResults(restoreList("稍后再看", Video.class,
                new RestoreCallback<Video>() {
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
                                "https://api.bilibili.com/x/v2/history/toview/add", Void.class)
                                .modify(
                                        new HashMap<String, String>() {{
                                            put("aid", String.valueOf(data.getAid()));
                                        }}
                                );
                        if (apiResult.isFail()) {
                            throw new BusinessException(apiResult);
                        }
                    }
                }));
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("稍后再看", "ToView");
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "稍后再看");
    }

    @Override
    public List<BusinessResult<List<Video>>> clear() throws BusinessException {
        return createResults(clearList("稍后再看", new ClearListCallback<Video>() {
            @Override
            public List<Video> getList() throws BusinessException {
                return ToViewService.this.getList();
            }

            @Override
            public void delData(Video data) throws BusinessException {
                ApiResult<Void> apiResult = new ModifyApi<Void>(client, user,
                        "https://api.bilibili.com/x/v2/history/toview/del", Void.class)
                        .modify(
                                new HashMap<String, String>() {{
                                    put("jsonp", "jsonp");
                                    put("aid", String.valueOf(data.getAid()));
                                }}
                        );
                if (apiResult.isFail()) {
                    throw new BusinessException(apiResult);
                }
            }

            @Override
            public String dataName(Video data) {
                return String.format("视频[%s]", data.getTitle());
            }
        }));
    }
}
