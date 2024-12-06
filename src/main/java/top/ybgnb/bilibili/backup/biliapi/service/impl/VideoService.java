package top.ybgnb.bilibili.backup.biliapi.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.Video;
import top.ybgnb.bilibili.backup.biliapi.bean.page.CursorPageData;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.AddQueryParams;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.service.BaseService;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.FileUtil;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName VideoService
 * @Description 投稿视频
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class VideoService extends BaseService {

    public VideoService(OkHttpClient client, User user) {
        super(client, user);
    }

    /**
     * 用户投稿的视频
     *
     * @param uid
     * @return
     */
    public List<Video> getVideos(String uid) throws BusinessException {
        log.info("获取用户[{}]投稿视频中...", user.getUid());
        List<Video> videos = new ArrayList<>();
        List<JSONObject> list = new PageApi<>(client, signUser(), "https://app.bilibili.com/x/v2/space/archive/cursor", new AddQueryParams() {
            @Override
            public void addQueryParams(Map<String, String> queryParams) {
                queryParams.put("vmid", uid);
            }
        }, CursorPageData.class, JSONObject.class)
                .getAllData((pageData, queryParams) -> {
                    if (pageData != null) {
                        List<JSONObject> allData = pageData._getList();
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
