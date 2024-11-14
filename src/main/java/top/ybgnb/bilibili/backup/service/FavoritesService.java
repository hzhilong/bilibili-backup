package top.ybgnb.bilibili.backup.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.bean.ApiResult;
import top.ybgnb.bilibili.backup.bean.FavFolder;
import top.ybgnb.bilibili.backup.bean.FavPageData;
import top.ybgnb.bilibili.backup.bean.Media;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.request.AddQueryParams;
import top.ybgnb.bilibili.backup.request.BaseApi;
import top.ybgnb.bilibili.backup.request.ListApi;
import top.ybgnb.bilibili.backup.request.ModifyApi;
import top.ybgnb.bilibili.backup.request.PageApi;
import top.ybgnb.bilibili.backup.user.User;
import top.ybgnb.bilibili.backup.utils.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName FavoritesService
 * @Description 收藏夹
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class FavoritesService extends BackupRestoreService {

    public FavoritesService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    private List<FavFolder> getFavFolders() throws BusinessException {
        return new ListApi<>(client, user,
                "https://api.bilibili.com/x/v3/fav/folder/created/list-all",
                queryParams -> {
                    queryParams.put("up_mid", user.getUid());
                }, FavFolder.class).getList();
    }

    public FavPageData getFavData(String mediaId) throws BusinessException {
        return new PageApi<>(client, user, "https://api.bilibili.com/x/v3/fav/resource/list",
                queryParams -> {
                    queryParams.put("media_id", mediaId);
                    queryParams.put("order", "mtime");
                    queryParams.put("type", "0");
                    queryParams.put("platform", "web");
                },
                FavPageData.class, Media.class).getAllPageData();
    }

    private String getFileName(FavFolder favFolder) {
        return favFolder.getTitle() + "-" + favFolder.getId();
    }

    @Override
    public void backup() throws BusinessException {
        List<FavFolder> favFolders = backupData("收藏夹/", "创建的收藏夹", new BackupCallback<List<FavFolder>>() {
            @Override
            public List<FavFolder> getData() throws BusinessException {
                return getFavFolders();
            }

            @Override
            public List<FavFolder> processData(List<FavFolder> list) throws BusinessException {
                for (FavFolder favFolder : list) {
                    log.info(String.format("获取收藏夹[%s]信息", favFolder.getTitle()));
                    ApiResult<FavFolder> apiResult = new BaseApi<FavFolder>(client, user,
                            "https://api.bilibili.com/x/v3/fav/folder/info", new AddQueryParams() {
                        @Override
                        public void addQueryParams(Map<String, String> queryParams) {
                            queryParams.put("media_id", String.valueOf(favFolder.getId()));
                        }
                    }, true, FavFolder.class).apiGet();
                    if (apiResult._isSuccess()) {
                        FavFolder favFolderInfo = apiResult.getData();
                        favFolder.setIntro(favFolderInfo.getIntro());
                        favFolder.setCover(favFolderInfo.getCover());
                    }
                }
                return list;
            }
        });
        for (FavFolder favFolder : favFolders) {
            backupData("收藏夹/", getFileName(favFolder),
                    () -> getFavData(String.valueOf(favFolder.getId())));
        }
    }

    @Override
    public void restore() throws BusinessException {
        log.info(String.format("正在还原收藏夹..."));

        restoreList("收藏夹/", "创建的收藏夹", FavFolder.class, new RestoreCallback<FavFolder>() {
            @Override
            public List<FavFolder> getNewList() throws BusinessException {
                return getFavFolders();
            }

            @Override
            public String compareFlag(FavFolder data) {
                return data.getTitle();
            }

            @Override
            public String dataName(FavFolder data) {
                return String.format("收藏夹[%s]", data.getTitle());
            }

            @Override
            public void restoreData(FavFolder data) throws BusinessException {
                if ((data.getAttr() >> 1 & 1) == 0) {
                    throw new BusinessException("默认收藏夹，无需创建");
                }
                ApiResult<FavFolder> apiResult = new ModifyApi<FavFolder>(client, user,
                        "https://api.bilibili.com/x/v3/fav/folder/add", FavFolder.class).modify(
                        new HashMap<String, String>() {{
                            put("title", data.getTitle());
                            put("intro", data.getIntro());
                            put("privacy", String.valueOf(data.getAttr() & 1));
                            put("cover", data.getCover());
                        }}
                );
                if (apiResult._isFail()) {
                    throw new BusinessException(apiResult);
                }
            }
        });


        List<FavFolder> oldFolders = JSONObject.parseArray(readJsonFile(path + "收藏夹/", "创建的收藏夹"), FavFolder.class);
        List<FavFolder> newFolders = getFavFolders();

        Map<String, FavFolder> mapNewFolders = new HashMap<>();
        Map<Long, Set<Long>> newUserFavMedias = new HashMap<>();
        for (FavFolder folder : newFolders) {
            mapNewFolders.put(folder.getTitle(), folder);
            FavPageData favData = getFavData(String.valueOf(folder.getId()));
            List<Media> medias = favData.getMedias();
            if (ListUtil.notEmpty(medias)) {
                Set<Long> ids = new HashSet<>();
                for (Media media : medias) {
                    ids.add(media.getId());
                }
                newUserFavMedias.put(folder.getId(), ids);
            }
        }


        log.info("正在解析需要收藏的视频...");
        Map<Long, List<Long>> videoFavNewIds = new HashMap<>();
        Map<Long, Media> mapMedias = new HashMap<>();
        for (FavFolder oldFolder : oldFolders) {
            String oldFolderTitle = oldFolder.getTitle();
            if (mapNewFolders.containsKey(oldFolderTitle)) {
                Long newFolderId = mapNewFolders.get(oldFolderTitle).getId();
                log.info("读取旧账号收藏夹列表：" + oldFolderTitle);
                FavPageData favData = JSONObject.parseObject(readJsonFile(path + "收藏夹/",
                        getFileName(oldFolder)), FavPageData.class);
                if (favData != null && ListUtil.notEmpty(favData.getMedias())) {
                    Collections.reverse(favData.getMedias());
                    for (Media media : favData.getMedias()) {
                        mapMedias.put(media.getId(), media);
                        List<Long> idList;
                        if (videoFavNewIds.containsKey(media.getId())) {
                            idList = videoFavNewIds.get(media.getId());
                            idList.add(newFolderId);
                        } else {
                            idList = new ArrayList<>();
                            idList.add(newFolderId);
                            videoFavNewIds.put(media.getId(), idList);
                        }
                    }
                }
            }
        }
        log.info(String.format("即将收藏%s个视频", videoFavNewIds.size()));
        for (Map.Entry<Long, List<Long>> entry : videoFavNewIds.entrySet()) {
            Long mediaId = entry.getKey();
            List<Long> folderIds = entry.getValue();
            Media media = mapMedias.get(mediaId);
            boolean isNeedAdd = false;
            for (Long folderId : folderIds) {
                if (!newUserFavMedias.containsKey(folderId) || !newUserFavMedias.get(folderId).contains(mediaId)) {
                    isNeedAdd = true;
                    break;
                }
            }
            if (!isNeedAdd) {
                log.info(String.format("[%s]已收藏", media.getTitle()));
            } else {
                ApiResult<Object> apiResult = new ModifyApi<>(client, user,
                        "https://api.bilibili.com/x/v3/fav/resource/deal", JSONObject.class).modify(
                        new HashMap<String, String>() {{
                            put("rid", String.valueOf(mediaId));
                            put("type", "2");
                            put("add_media_ids", ListUtil.listToString(folderIds, ","));
                            put("del_media_ids", "");
                            put("platform", "web");
                            put("eab_x", "1");
                            put("ramval", "10");
                            put("ga", "1");
                            put("gaia_source", "web_normal");
                        }}
                );
                if (apiResult._isFail()) {
                    log.info(String.format("收藏[%s]失败：%s(%s)", media.getTitle(), apiResult.getMessage(), apiResult.getCode()));
                } else {
                    log.info(String.format("收藏[%s]成功", media.getTitle()));
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }

        }
    }


}
