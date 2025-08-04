package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.FileUtil;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.DM;
import io.github.hzhilong.bilibili.backup.api.bean.Video;
import io.github.hzhilong.bilibili.backup.api.bean.VideoPart;
import io.github.hzhilong.bilibili.backup.api.bean.page.CursorPageData;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.ProtoCallback;
import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 视频稿件
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class VideoService extends BaseService {

    private PageApi<CursorPageData, JSONObject> pageApi;

    public VideoService(OkHttpClient client, User user) {
        super(client, user);
    }

    /**
     * 用户投稿的视频
     */
    public List<Video> getVideos(String uid) throws BusinessException {
        log.info("获取用户[{}]投稿视频中...", uid);
        List<Video> videos = new ArrayList<>();
        pageApi = new PageApi<>(client, signUser(), "https://app.bilibili.com/x/v2/space/archive/cursor",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("vmid", uid);
                    }
                }, CursorPageData.class, JSONObject.class);
        List<JSONObject> list = pageApi
                .getAllData((pageData, queryParams) -> {
                    try {
                        handleInterrupt();
                    } catch (BusinessException e) {
                        throw new RuntimeException(e);
                    }
                    if (pageData != null) {
                        List<JSONObject> allData = pageData.getList();
                        if (ListUtil.notEmpty(allData)) {
                            queryParams.put("aid", String.valueOf(allData.get(allData.size() - 1).getString("param")));
                        }
                    }
                });
        if (ListUtil.isEmpty(list)) {
            return videos;
        }
        for (JSONObject json : list) {
            Video video = new Video();
            video.setAid(Long.valueOf(json.getString("param")));
            video.setTitle(json.getString("title"));
            video.setBvid(json.getString("bvid"));
            videos.add(video);
        }
        return videos;
    }

    @Override
    protected void handleInterrupt() throws BusinessException {
        if (pageApi != null) {
            pageApi.setInterrupt(interrupt);
        }
        super.handleInterrupt();
    }

    public void backup(String path, List<Video> videos) throws BusinessException {
        String fileName = "投稿的视频.json";
        FileUtil.writeJsonFile(path, fileName, videos);
    }


    public List<VideoPart> getParts(String bvid) throws BusinessException {
        ApiResult<JSONArray> apiResult = new BaseApi<JSONArray>(client, user,
                "https://api.bilibili.com/x/player/pagelist",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("bvid", bvid);
                    }
                }, true, JSONArray.class).apiGet();
        if (apiResult.isSuccess()) {
            return apiResult.getData().toJavaList(VideoPart.class);
        } else {
            throw new ApiException(apiResult);
        }
    }

    @NotNull
    private BaseApi<DM.DmSegMobileReply> getDmSegMobileReplyBaseApi(VideoPart part, int index) {
        int finalIndex = index;
        BaseApi<DM.DmSegMobileReply> api = new BaseApi<DM.DmSegMobileReply>(client, user,
                "https://api.bilibili.com/x/v2/dm/web/seg.so",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("type", "1");
                        queryParams.put("oid", String.valueOf(part.getCid()));
                        queryParams.put("segment_index", String.valueOf(finalIndex));
                    }
                }, true, DM.DmSegMobileReply.class);
        return api;
    }

    public List<DM.DanmakuElem> getDM(VideoPart part) throws BusinessException {
        int segCount = (part.getDuration() - 1) / 360 + 1;
        List<DM.DanmakuElem> list = new ArrayList<>(128);
        for (int index = 1; index <= segCount; index++) {
            BaseApi<DM.DmSegMobileReply> api = getDmSegMobileReplyBaseApi(part, index);
            api.setProtoCallback(new ProtoCallback<DM.DmSegMobileReply>() {
                @Override
                public DM.DmSegMobileReply parse(InputStream input) {
                    try {
                        return DM.DmSegMobileReply.parseFrom(input);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            ApiResult<DM.DmSegMobileReply> apiResult = api.apiGet();
            if (apiResult.isSuccess()) {
                DM.DmSegMobileReply data = apiResult.getData();
                if (data != null && ListUtil.notEmpty(data.getElemsList())) {
                    list.addAll(data.getElemsList());
                }
            } else {
                throw new ApiException(apiResult);
            }
        }
        return list;
    }
}
