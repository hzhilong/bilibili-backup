package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.FavFolder;
import io.github.hzhilong.bilibili.backup.api.bean.Media;
import io.github.hzhilong.bilibili.backup.api.bean.page.FavPageData;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.api.request.ListApi;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BusinessResult;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreService;
import io.github.hzhilong.bilibili.backup.gui.dialog.FavFolderSelectDialog;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.collections4.ListUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 收藏夹
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class FavoritesService extends BackupRestoreService<FavFolder> implements NeedContext {

    @Setter
    private Window parentWindow;

    @Setter
    private String appIconPath;

    @Setter
    private boolean saveToDefaultOnFailure = false;

    private PageApi<FavPageData, Media> pageApi;

    public FavoritesService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    private List<FavFolder> getFavFolders() throws BusinessException {
        return new ListApi<>(client, signUser(),
                "https://api.bilibili.com/x/v3/fav/folder/created/list-all",
                queryParams -> {
                    queryParams.put("up_mid", user.getUid());
                }, FavFolder.class).getList();
    }

    private void addFavFolder(FavFolder data) throws BusinessException {
        if ((data.getAttr() >> 1 & 1) == 0) {
            // 默认收藏夹，无需创建
            return;
        }
        ApiResult<FavFolder> apiResult = new ModifyApi<FavFolder>(client, user,
                "https://api.bilibili.com/x/v3/fav/folder/add", FavFolder.class)
                .modify(
                        new HashMap<String, String>() {{
                            put("title", data.getTitle());
                            put("intro", data.getIntro());
                            put("privacy", String.valueOf(data.getAttr() & 1));
                            put("cover", data.getCover());
                        }}
                );
        if (apiResult.isFail()) {
            if (saveToDefaultOnFailure && (apiResult.getCode() == 11002 || apiResult.getMessage().contains("已达到数量上限"))) {
                data.setSaveToDefault(true);
                log.info("收藏夹数量已达到上限，[{}]内的视频将被保存到默认收藏夹", data.getTitle());
                return;
            }
            throw new ApiException(apiResult);
        }
    }

    public FavPageData getFavData(Long mediaId) throws BusinessException {
        return getFavData(String.valueOf(mediaId));
    }

    public FavPageData getFavData(String mediaId) throws BusinessException {
        pageApi = new PageApi<>(client, signUser(), "https://api.bilibili.com/x/v3/fav/resource/list",
                queryParams -> {
                    queryParams.put("media_id", mediaId);
                    queryParams.put("order", "mtime");
                    queryParams.put("type", "0");
                    queryParams.put("platform", "web");
                    queryParams.put("ps", "40");
                },
                FavPageData.class, Media.class);
        return pageApi.getAllPageData();
    }

    private String getOldFileName(FavFolder favFolder) {
        return favFolder.getTitle() + "-" + favFolder.getId();
    }

    private String getFileName(FavFolder favFolder) {
//        return favFolder.getTitle() + "-" + favFolder.getId();
        return String.valueOf(favFolder.getId());
    }

    @Override
    public List<BusinessResult<List<FavFolder>>> backup() throws BusinessException {
        BusinessResult<List<FavFolder>> folderResult = backupData("收藏夹", "创建的收藏夹",
                new BackupCallback<List<FavFolder>>() {
                    @Override
                    public List<FavFolder> getData() throws BusinessException {
                        return getFavFolders();
                    }

                    @Override
                    public List<FavFolder> processData(List<FavFolder> list) throws BusinessException {
                        if (parentWindow != null) {
                            FavFolderSelectDialog dialog = new FavFolderSelectDialog(parentWindow, appIconPath, list);
                            dialog.setVisible(true);
                            list = dialog.getSelectedList();
                            if (list == null) {
                                throw new BusinessException("未选择收藏夹");
                            }
                        }
                        for (FavFolder favFolder : list) {
                            log.info("获取收藏夹[{}]的信息", favFolder.getTitle());
                            ApiResult<FavFolder> apiResult = new BaseApi<FavFolder>(client, signUser(),
                                    "https://api.bilibili.com/x/v3/fav/folder/info",
                                    new AddQueryParams() {
                                        @Override
                                        public void addQueryParams(Map<String, String> queryParams) {
                                            queryParams.put("media_id", String.valueOf(favFolder.getId()));
                                        }
                                    }, true, FavFolder.class).apiGet();
                            if (apiResult.isSuccess()) {
                                FavFolder favFolderInfo = apiResult.getData();
                                favFolder.setIntro(favFolderInfo.getIntro());
                                favFolder.setCover(favFolderInfo.getCover());
                                log.info("收藏夹[{}]的视频数：{}", favFolder.getTitle(), favFolder.getMediaCount());
                            }
                        }
                        return list;
                    }
                });
        if (folderResult.isFail() || ListUtil.isEmpty(folderResult.getData())) {
            return createResults(folderResult);
        }
        List<FavFolder> favFolders = folderResult.getData();
        List<BusinessResult<List<FavFolder>>> results = new ArrayList<>(favFolders.size());
        for (FavFolder favFolder : favFolders) {
            BusinessResult<List<FavFolder>> tempResult = new BusinessResult<>();
            tempResult.setItemName(folderResult.getItemName());
            tempResult.setBusinessType(folderResult.getBusinessType());
            tempResult.setData(Collections.singletonList(favFolder));
            String title = favFolder.getTitle();
            try {
                log.info("正在备份收藏夹[{}]...", title);
                BusinessResult<FavPageData> favPageResult = backupData("收藏夹", getFileName(favFolder),
                        new BackupCallback<FavPageData>() {
                            @Override
                            public FavPageData getData() throws BusinessException {
                                return getFavData(favFolder.getId());
                            }
                        }, false, false);
                FavPageData favPageData = favPageResult.getData();
                favFolder.setMedias(favPageData.getMedias());
                Integer oldSize = favFolder.getMediaCount();
                int backupSize = ListUtil.getSize(favFolder.getMedias());
                if (oldSize == backupSize) {
                    String msg = String.format("收藏夹[%s](%s)备份成功，已备份%s个视频", title, oldSize, backupSize);
                    log.info(msg);
                    tempResult.setSuccess(msg);
                } else {
                    String msg = String.format("收藏夹[%s](%s)备份失败，已备份%s个视频", title, oldSize, backupSize);
                    log.info(msg);
                    tempResult.setFailed(msg);
                }
            } catch (BusinessException e) {
                String msg = String.format("收藏夹[%s]备份失败：%s", title, e.getMessage());
                log.info(msg);
                tempResult.setFailed(msg);
            }
            results.add(tempResult);
        }
        return results;
    }

    @Override
    public List<BusinessResult<List<FavFolder>>> restore() throws BusinessException {
        log.info("正在还原收藏夹...");
        BusinessResult<List<FavFolder>> folderResult = restoreList(
                "收藏夹", "创建的收藏夹", FavFolder.class,
                new RestoreCallback<FavFolder>() {
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
                        return getDataName(data);
                    }

                    @Override
                    public void restoreData(FavFolder data) throws BusinessException {
                        addFavFolder(data);
                    }

                });
        List<FavFolder> newFolders = folderResult.getData();
        if (folderResult.isFail() || ListUtil.isEmpty(folderResult.getData())) {
            return createResults(folderResult);
        }
        Set<String> saveToDefaultMap = new HashSet<>(newFolders.size());
        for (FavFolder favFolder : newFolders) {
            if (favFolder != null && favFolder.isSaveToDefault()) {
                saveToDefaultMap.add(favFolder.getTitle());
            }
        }

        List<BusinessResult<List<FavFolder>>> results = new ArrayList<>(newFolders.size());
        // 需获取一下收藏夹，因为id和备份的不一样
        newFolders = getFavFolders();
        FavFolder defaultFolder = null;
        for (FavFolder favFolder : newFolders) {
            if (favFolder.isDefault()) {
                defaultFolder = favFolder;
            }
        }

        List<FavFolder> oldFolders = JSONObject.parseArray(readJsonFile(path, "收藏夹", "创建的收藏夹"), FavFolder.class);

        if (parentWindow != null) {
            FavFolderSelectDialog dialog = new FavFolderSelectDialog(parentWindow, appIconPath, oldFolders);
            dialog.setVisible(true);
            oldFolders = dialog.getSelectedList();
            if (oldFolders == null) {
                throw new BusinessException("未选择收藏夹");
            }
        }

        Map<String, FavFolder> mapNewFolders = new HashMap<>();
        for (String title : saveToDefaultMap) {
            mapNewFolders.put(title, defaultFolder);
        }
        Map<Long, Set<Long>> newUserFavMedias = new HashMap<>();
        log.info("获取新账号的数据...");
        String logNoFormat2 = StringUtils.getLogNoFormat(newFolders.size());
        for (int i = 0; i < newFolders.size(); i++) {
            handleInterrupt();
            FavFolder folder = newFolders.get(i);
            String title = folder.getTitle();
            mapNewFolders.put(title, folder);
            if (!isDirectRestore()) {
                if (oldFolders != null) {
                    boolean needGetNewData = true;
                    for (FavFolder oldFolder : oldFolders) {
                        if (!oldFolder.getTitle().equals(folder.getTitle())) {
                            needGetNewData = false;
                        }
                    }
                    if (!needGetNewData) {
                        log.info("{}新账号收藏夹[{}]不在还原的数据内，跳过", String.format(logNoFormat2, i + 1), folder.getTitle());
                        continue;
                    }
                }
                log.info("{}获取新账号收藏夹[{}]的内容...", String.format(logNoFormat2, i + 1), folder.getTitle());
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
        }

        log.info("正在解析需要收藏的视频...");
        Map<Long, List<Long>> videoFavNewIds = new LinkedHashMap<>();
        Map<Long, Media> mapMedias = new HashMap<>();
        for (FavFolder oldFolder : oldFolders) {
            handleInterrupt();

            String oldFolderTitle = oldFolder.getTitle();
            BusinessResult<List<FavFolder>> result = new BusinessResult<>();
            result.setBusinessType(BusinessType.RESTORE);
            result.setItemName("收藏夹：" + oldFolderTitle);
            result.setSuccess(true);

            if (mapNewFolders.containsKey(oldFolderTitle)) {
                Long newFolderId = mapNewFolders.get(oldFolderTitle).getId();
                log.info("读取旧账号收藏夹列表：{}", oldFolderTitle);
                FavPageData favData;
                try {
                    favData = JSONObject.parseObject(readJsonFile(path, "收藏夹",
                            getFileName(oldFolder)), FavPageData.class);
                } catch (BusinessException ex) {
                    favData = JSONObject.parseObject(readJsonFile(path, "收藏夹",
                            getOldFileName(oldFolder)), FavPageData.class);
                }
                oldFolder.setMedias(favData.getMedias());
                result.setData(Collections.singletonList(oldFolder));

                if (ListUtil.notEmpty(favData.getMedias())) {
                    Collections.reverse(favData.getMedias());
                    for (Media media : favData.getMedias()) {
                        mapMedias.put(media.getId(), media);
                        List<Long> idList;
                        // 解析数据：旧收藏id<>旧收藏夹ids
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
            } else {
                result.setFailed("备份失败，新创建的收藏夹不存在" + oldFolderTitle);
            }
            results.add(result);
        }
        log.info("即将收藏{}个视频", videoFavNewIds.size());
        Set<Long> failIds = new HashSet<>();
        int i = 0;
        String logNoFormat = StringUtils.getLogNoFormat(videoFavNewIds.size());
        for (Map.Entry<Long, List<Long>> entry : videoFavNewIds.entrySet()) {
            i++;
            handleInterrupt();
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
                log.info("{}[{}]已收藏", String.format(logNoFormat, i), media.getTitle());
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
                if (apiResult.isFail()) {
                    log.info("{}收藏[{}]失败：{}({})", String.format(logNoFormat, i), media.getTitle(), apiResult.getMessage(), apiResult.getCode());
                    failIds.add(mediaId);
                } else {
                    log.info("{}收藏[{}]成功", String.format(logNoFormat, i), media.getTitle());
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }
        // 统计结果
        for (BusinessResult<List<FavFolder>> result : results) {
            if (result.isSuccess()) {
                FavFolder favFolder = result.getData().get(0);
                List<Media> medias = favFolder.getMedias();
                String title = favFolder.getTitle();
                if (ListUtil.isEmpty(medias)) {
                    result.setFailed("[" + title + "]收藏夹为空");
                } else {
                    medias.removeIf(media -> failIds.contains(media.getId()));
                    Integer oldSize = favFolder.getMediaCount();
                    int newSize = medias.size();
                    if (oldSize == newSize) {
                        String msg = String.format("收藏夹[%s](%s)还原成功，已还原%s个视频", title, oldSize, newSize);
                        result.setSuccess(msg);
                    } else {
                        String msg = String.format("收藏夹[%s](%s)还原失败，已还原%s个视频", title, oldSize, newSize);
                        result.setFailed(msg);
                    }
                }
            }
        }
        return results;
    }

    @NotNull
    private static String getDataName(FavFolder data) {
        return String.format("收藏夹[%s]", data.getTitle());
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("收藏夹", "Favorites");
        fileNames.put("创建的收藏夹", "CreatedFavorites");
    }

    private BusinessResult<List<FavFolder>> buildClearResult(String buName, boolean success, String msg) {
        BusinessResult<List<FavFolder>> result = new BusinessResult<>();
        result.setBusinessType(BusinessType.CLEAR);
        result.setItemName(buName);
        if (success) {
            result.setSuccess(msg);
        } else {
            result.setFailed(msg);
        }
        return result;
    }

    @Override
    public List<BusinessResult<List<FavFolder>>> clear() throws BusinessException {
        String buName = "收藏夹";
        log.info("正在清空{}...", buName);
        List<BusinessResult<List<FavFolder>>> results = new ArrayList<>();
        List<FavFolder> favFolders = getFavFolders();
        if (ListUtil.isEmpty(favFolders)) {
            results.add(buildClearResult(buName, false, "数据为空，无需清空"));
            return results;
        }
        String formatFolder = StringUtils.getLogNoFormat(favFolders.size());
        for (int i = 0; i < favFolders.size(); i++) {
            FavFolder favFolder = favFolders.get(i);
            String favFolderName = getDataName(favFolder);
            log.info("{}{}", String.format(formatFolder, i + 1), favFolderName);
            if (favFolder.isDefault()) {
                FavPageData favPageData = getFavData(favFolder.getId());
                if (favPageData == null || ListUtil.isEmpty(favPageData.getList())) {
                    results.add(buildClearResult(favFolderName, false, "收藏夹为空，无需清空"));
                } else {
                    List<List<Media>> partition = ListUtils.partition(favPageData.getList(), 40);
                    for (int i1 = 0; i1 < partition.size(); i1++) {
                        List<Media> list = partition.get(i1);
                        log.info("正在删除第{}页，共{}条数据...", i1 + 1, list.size());
                        String resources = list.stream().map(new Function<Media, String>() {
                            @Override
                            public String apply(Media media) {
                                return String.format("%s:%s", media.getId(), media.getType());
                            }
                        }).collect(Collectors.joining(","));
                        ApiResult<Object> apiResult = new ModifyApi<Object>(client, user,
                                "https://api.bilibili.com/x/v3/fav/resource/batch-del", Object.class)
                                .modify(
                                        new HashMap<String, String>() {{
                                            put("resources", resources);
                                            put("platform", "web");
                                            put("media_id", String.valueOf(favPageData.getInfo().getId()));
                                        }}
                                );
                        if (apiResult.isFail()) {
                            results.add(buildClearResult(favFolderName, false, "批量取消收藏失败：" + apiResult.getMessage()));
                            break;
                        }
                    }
                    results.add(buildClearResult(favFolderName, true, "成功清空" + favPageData.getList().size() + "条数据"));
                }
            } else {
                log.info("正在删除{}...", favFolderName);
                ApiResult<Object> apiResult = new ModifyApi<Object>(client, user,
                        "https://api.bilibili.com/x/v3/fav/folder/del", Object.class)
                        .modify(
                                new HashMap<String, String>() {{
                                    put("platform", "web");
                                    put("media_ids", String.valueOf(favFolder.getId()));
                                }}
                        );
                if (apiResult.isFail()) {
                    throw new ApiException(apiResult);
                }
                results.add(buildClearResult(favFolderName, true, "清空成功"));
            }
        }
        return results;
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "收藏夹", "创建的收藏夹");
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        if (pageApi != null) {
            pageApi.setInterrupt(interrupt);
        }
        super.setInterrupt(interrupt);
    }
}
