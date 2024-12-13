package io.github.hzhilong.bilibili.backup.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.bean.Video;
import io.github.hzhilong.bilibili.backup.api.bean.page.CursorPageData;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.service.BaseService;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.base.utils.FileUtil;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.error.BusinessException;

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

    public VideoService(OkHttpClient client, User user) {
        super(client, user);
    }

    /**
     * 用户投稿的视频
     */
    public List<Video> getVideos(String uid) throws BusinessException {
        log.info("获取用户[{}]投稿视频中...", user.getUid());
        List<Video> videos = new ArrayList<>();
        List<JSONObject> list = new PageApi<>(client, signUser(), "https://app.bilibili.com/x/v2/space/archive/cursor",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("vmid", uid);
                    }
                }, CursorPageData.class, JSONObject.class)
                .getAllData((pageData, queryParams) -> {
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

    public void backup(String path, List<Video> videos) throws BusinessException {
        String fileName = "投稿的视频.json";
        FileUtil.writeJsonFile(path, fileName, videos);
    }
}
