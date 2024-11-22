package top.ybgnb.bilibili.backup.biliapi.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.Opus;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.AddQueryParams;
import top.ybgnb.bilibili.backup.biliapi.request.BaseApi;
import top.ybgnb.bilibili.backup.biliapi.request.ModifyApi;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OpusesService
 * @Description 收藏的专栏
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class FavOpusesService extends BackupRestoreService {

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
    public void backup() throws BusinessException {
        backupData("收藏的专栏", () -> getList());
    }

    @Override
    public void restore() throws BusinessException {
        restoreList("收藏的专栏", Opus.class, new RestoreCallback<Opus>() {
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
                log.info(String.format("获取%s信息...", dataName(data)));
                ApiResult<JSONObject> retInfo = new BaseApi<JSONObject>(client, user,
                        "https://api.bilibili.com/x/polymer/web-dynamic/v1/opus/detail",
                        new AddQueryParams() {
                            @Override
                            public void addQueryParams(Map<String, String> queryParams) {
                                queryParams.put("id", String.valueOf(data.getOpusId()));
                            }
                        }, true, JSONObject.class).apiGet();
                if (retInfo._isFail()) {
                    throw new BusinessException(retInfo);
                }
                String id = retInfo.getData().getJSONObject("fallback").getString("id");
                if (StringUtils.isEmpty(id)) {
                    throw new BusinessException(String.format("获取%s信息失败", dataName(data)));
                }
                ApiResult<JSONObject> apiResult = new ModifyApi<JSONObject>(client, user,
                        "https://api.bilibili.com/x/article/favorites/add",
                        JSONObject.class)
                        .modify(new HashMap<String, String>() {{
                            put("id", id);
                        }});
                if (apiResult._isFail()) {
                    throw new BusinessException(apiResult);
                }
            }
        });
    }
}
