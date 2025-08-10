package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.FavFolder;
import io.github.hzhilong.bilibili.backup.api.bean.Video;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavoritesService;
import io.github.hzhilong.bilibili.backup.app.service.impl.VideoService;
import io.github.hzhilong.bilibili.backup.gui.utils.CommonDialog;
import io.github.hzhilong.bilibili.backup.gui.utils.FavFolderUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 收藏所有视频
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class FavAllVideosRunnable extends ToolRunnable<BaseService, Void> implements NeedContext {

    private VideoService videoService;
    private FavoritesService favoritesService;

    @Setter
    private Window parentWindow;
    @Setter
    private String appIconPath;

    public FavAllVideosRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback) {
        super(client, user, buCallback);
    }

    @Override
    protected void newServices(LinkedHashSet<BaseService> services) {
        videoService = new VideoService(client, new User(user.getCookie()));
        services.add(videoService);
        favoritesService = new FavoritesService(client, new User(user.getCookie()), "");
        services.add(favoritesService);
    }

    @Override
    protected Void runTool() throws BusinessException {
        String uid = CommonDialog.getUid(parentWindow);
        List<Video> videos = videoService.getVideos(uid);
        if (ListUtil.isEmpty(videos)) {
            throw new BusinessException("该用户投稿的视频为空");
        }
        log.info("已获取{}个视频", videos.size());

        handleInterrupt();
        log.info("获取当前账号的收藏夹...");
        FavFolder tarFav = FavFolderUtils.chooseFavFolder(parentWindow, appIconPath, favoritesService, videos.size());

        Collections.reverse(videos);
        log.info("即将收藏{}个视频", videos.size());
        String logNoFormat = StringUtils.getLogNoFormat(videos.size());
        int errCount = 0;
        for (int i = 1; i <= videos.size(); i++) {
            handleInterrupt();
            Video video = videos.get(i - 1);
            ApiResult<JSONObject> apiResult = favoritesService.favVideo(String.valueOf(video.getAid()), String.valueOf(tarFav.getId()), "");
            if (apiResult.isFail()) {
                log.info("{}收藏[{}]失败：{}({})", String.format(logNoFormat, i), video.getTitle(), apiResult.getMessage(), apiResult.getCode());
                if (FavFolderUtils.isFavFull(apiResult)) {
                    log.info("该收藏夹已满，切换收藏夹...");
                    tarFav = FavFolderUtils.chooseFavFolder(parentWindow, appIconPath, favoritesService, videos.size() - i + 1);
                    log.info("已切换收藏夹，等待继续...");
                    i--;
                    continue;
                } else {
                    if (errCount > 5) {
                        throw new BusinessException("多次失败，暂停操作");
                    }
                    errCount++;
                }
            } else {
                log.info("{}收藏[{}]成功", String.format(logNoFormat, i), video.getTitle());
            }
            randomSleep(1333, 2000);
        }
        return null;
    }


}
