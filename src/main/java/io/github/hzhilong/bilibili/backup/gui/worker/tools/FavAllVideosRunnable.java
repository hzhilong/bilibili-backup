package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.FavFolder;
import io.github.hzhilong.bilibili.backup.api.bean.FavInfo;
import io.github.hzhilong.bilibili.backup.api.bean.Video;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavoritesService;
import io.github.hzhilong.bilibili.backup.app.service.impl.VideoService;
import io.github.hzhilong.bilibili.backup.gui.dialog.FavFolderSelectDialog;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
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
        // 输入uid
        String uid = getUid();
        // 获取收藏夹
        List<Video> videos = videoService.getVideos(uid);
        if (ListUtil.isEmpty(videos)) {
            throw new BusinessException("该用户投稿的视频为空");
        }
        log.info("已获取{}个视频", videos.size());

        handleInterrupt();
        log.info("获取当前账号的收藏夹中...");
        List<FavFolder> favFolders = getFavFolders();
        FavInfo tarFav;
        while (true) {
            tarFav = chooseFavFolder(favFolders);
            if (tarFav.getRemainingCount() < videos.size()) {
                JOptionPane.showMessageDialog(parentWindow, "当前收藏夹剩余空间不够", "提示", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        Collections.reverse(videos);
        log.info("即将收藏{}个视频", videos.size());
        String logNoFormat = StringUtils.getLogNoFormat(videos.size());
        for (int i = 1; i <= videos.size(); i++) {
            handleInterrupt();
            Video video = videos.get(i - 1);
            ApiResult<JSONObject> apiResult = favoritesService.favVideo(String.valueOf(video.getAid()), String.valueOf(tarFav.getId()), "");
            if (apiResult.isFail()) {
                log.info("{}收藏[{}]失败：{}({})", String.format(logNoFormat, i), video.getTitle(), apiResult.getMessage(), apiResult.getCode());
            } else {
                log.info("{}收藏[{}]成功", String.format(logNoFormat, i), video.getTitle());
            }
            try {
                Thread.sleep(1333);
            } catch (InterruptedException ignored) {
            }
        }

        return null;
    }

    private String getUid() throws BusinessException {
        String uid = JOptionPane.showInputDialog(parentWindow, "请输入对方的UID：",
                "提示", JOptionPane.QUESTION_MESSAGE);
        if (StringUtils.isEmpty(uid)) {
            JOptionPane.showMessageDialog(parentWindow, "请输入用户UID！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("请输入用户UID！");
        } else if (!AppConstant.NUM_PATTERN.matcher(uid).find()) {
            JOptionPane.showMessageDialog(parentWindow, "用户UID为纯数字！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("用户UID为纯数字！");
        }
        return uid;
    }

    private List<FavFolder> getFavFolders() throws BusinessException {
        List<FavFolder> favFolders = favoritesService.getFavFolders();
        if (ListUtil.isEmpty(favFolders)) {
            throw new BusinessException("当前账号无收藏夹（默认收藏夹咋也没了...）");
        }
        return favFolders;
    }

    private FavFolder chooseFavFolder(List<FavFolder> favFolders) throws BusinessException {
        FavFolderSelectDialog dialog = new FavFolderSelectDialog(parentWindow, appIconPath, favFolders, true);
        dialog.setTitle("收藏至：");
        dialog.setVisible(true);
        favFolders = dialog.getSelectedList();
        if (ListUtil.isEmpty(favFolders)) {
            throw new BusinessException("未选择收藏夹");
        }
        return favFolders.get(0);
    }
}
