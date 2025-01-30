package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.Opus;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
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
 * 收藏的专栏
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class FavOpusesService extends BackupRestoreService<Opus> {

    public FavOpusesService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    public List<Opus> getList() throws BusinessException {
        return new PageApi<>(client, user, "https://api.bilibili.com/x/polymer/web-dynamic/v1/opus/favlist",
                queryParams -> {
                    queryParams.put("page", queryParams.remove("pn"));
                    queryParams.remove("ps");
                    queryParams.put("page_size", "10");
                }, Opus.class).getAllData();
    }

    @Override
    public List<BusinessResult<List<Opus>>> backup() throws BusinessException {
        return createResults(backupData("收藏的专栏", this::getList));
    }

    @Override
    public List<BusinessResult<List<Opus>>> restore() throws BusinessException {
        return createResults(restoreList("收藏的专栏", Opus.class,
                new RestoreCallback<Opus>() {
                    @Override
                    public List<Opus> getNewList() throws BusinessException {
                        return getList();
                    }

                    @Override
                    public String compareFlag(Opus data) {
                        return String.valueOf(data.getOpusId());
                    }

                    @Override
                    public String dataName(Opus data) {
                        return String.format("专栏[%s]", data.getTitle());
                    }

                    @Override
                    public void restoreData(Opus data) throws BusinessException {
                        log.info("获取{}信息...", dataName(data));
                        ApiResult<JSONObject> retInfo = new BaseApi<JSONObject>(client, user,
                                "https://api.bilibili.com/x/polymer/web-dynamic/v1/opus/detail",
                                new AddQueryParams() {
                                    @Override
                                    public void addQueryParams(Map<String, String> queryParams) {
                                        queryParams.put("id", String.valueOf(data.getOpusId()));
                                    }
                                },
                                true, JSONObject.class).apiGet();
                        if (retInfo.isFail()) {
                            throw new ApiException(retInfo);
                        }
                        JSONObject retInfoData = retInfo.getData();
                        if (retInfoData == null || !retInfoData.containsKey("fallback")) {
                            throw new BusinessException(String.format("获取%s信息失败", dataName(data)));
                        }
                        String id = retInfoData.getJSONObject("fallback").getString("id");
                        if (StringUtils.isEmpty(id)) {
                            throw new BusinessException(String.format("获取%s信息失败", dataName(data)));
                        }

                        ApiResult<JSONObject> apiResult = new ModifyApi<JSONObject>(client, user,
                                "https://api.bilibili.com/x/article/favorites/add",
                                JSONObject.class)
                                .modify(new HashMap<String, String>() {{
                                    put("id", id);
                                }});
                        if (apiResult.isFail()) {
                            throw new ApiException(apiResult);
                        }
                    }
                }));
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("收藏的专栏", "FavOpuses");
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "收藏的专栏");
    }

    @Override
    public List<BusinessResult<List<Opus>>> clear() throws BusinessException {
        return createResults(clearList("收藏的专栏", new ClearListCallback<Opus>() {
            @Override
            public List<Opus> getList() throws BusinessException {
                return FavOpusesService.this.getList();
            }

            @Override
            public void delData(Opus data) throws BusinessException {
                JSONObject reqData = new JSONObject();
                reqData.put("action", 4);
                reqData.put("entity", new JSONObject() {{
                    put("object_id_str", String.valueOf(data.getOpusId()));
                    put("type", new JSONObject() {{
                        put("biz", 2);
                    }});
                }});
                reqData.put("", new JSONObject() {{
                    put("from", "unknown");
                    put("from_spmid", "333.1387.0.0");
                    put("spmid", "444.42.0.0");
                }});
                ApiResult<Object> apiResult = new BaseApi<Object>(client, user,
                        "https://api.bilibili.com/x/community/cosmo/interface/simple_action",
                        new AddQueryParams() {
                            @Override
                            public void addQueryParams(Map<String, String> queryParams) {
                                queryParams.put("csrf", user.getBiliJct());
                            }
                        }, true, Object.class)
                        .apiPost(reqData.toJSONString());
                if (apiResult.isFail()) {
                    throw new ApiException(apiResult);
                }
            }

            @Override
            public String dataName(Opus data) {
                return String.format("专栏[%s]", data.getTitle());
            }
        }));
    }
}
