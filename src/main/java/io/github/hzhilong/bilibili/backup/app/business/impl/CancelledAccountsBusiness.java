package io.github.hzhilong.bilibili.backup.app.business.impl;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.app.business.BaseBusiness;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.cli.BaseMenu;
import io.github.hzhilong.bilibili.backup.app.cli.UserMenu;
import io.github.hzhilong.bilibili.backup.app.cli.btn.callback.YesOrNo;
import io.github.hzhilong.bilibili.backup.api.bean.CancelledAccountInfo;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.bilibili.backup.api.bean.Video;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.bean.ServiceBuilder;
import io.github.hzhilong.bilibili.backup.app.service.impl.BangumiService;
import io.github.hzhilong.bilibili.backup.app.service.impl.CancelledAccountService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavCollectedService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavoritesService;
import io.github.hzhilong.bilibili.backup.app.service.impl.VideoService;
import io.github.hzhilong.bilibili.backup.api.user.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * 已注销账号数据
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class CancelledAccountsBusiness extends BaseBusiness {

    @Override
    public Upper process(Scanner scanner) throws BusinessException {
        // 输入用户UID
        String uid = UserMenu.inputUid(scanner);
        // 设置当前备份目录
        String path = String.format("%s%s_%s_%s/", AppConstant.BACKUP_PATH_PREFIX, AppConstant.CANCELLED_ACCOUNT_NAME, uid,
                new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        getUserInfo(uid);
        log.info("\n是否获取该用户投稿的视频数据？");
        BaseMenu.inputYes(scanner, new YesOrNo() {
            @Override
            public void yes() {
                try {
                    getUserVideos(uid, path);
                } catch (BusinessException e) {
                    log.info(e.getMessage());
                }
            }

            @Override
            public void no() {

            }
        });
        log.info("\n是否尝试备份该用户数据？(需在注销前将相关隐私设置为公开)");
        log.info("\t备份的项目有：收藏夹、收藏的视频合集、追番追剧\n");
        final Upper[] upper = {null};
        BaseMenu.inputYes(scanner, new YesOrNo() {
            @Override
            public void yes() {
                backupData(uid, path);
                upper[0] = new Upper(Long.valueOf(uid), AppConstant.CANCELLED_ACCOUNT_NAME, "");
            }

            @Override
            public void no() {

            }
        });
        return upper[0];
    }

    private void getUserInfo(String uid) throws BusinessException {
        CancelledAccountInfo info = new CancelledAccountService(client, new User(uid)).getInfo();
        log.info("");
        log.info("用户UID：{}", uid);
        log.info("该用户关注数：{}", info.getFollowingCount());
        log.info("该用户粉丝数：{}", info.getFollowerCount());
    }

    private void getUserVideos(String uid, String path) throws BusinessException {
        log.info("正在获取该用户投稿的视频，请稍候...");
        VideoService videoService = new VideoService(client, new User(uid));
        List<Video> videos = videoService.getVideos(uid);
        log.info("该用户共投稿{}个视频。", videos.size());
        for (int i = 0; i < videos.size(); i++) {
            Video video = videos.get(i);
            log.info("{}.{} {}", i + 1, video.getBvid(), video.getTitle());
        }
        videoService.backup(path, videos);
    }

    private void backupData(String uid, String path) {
        List<ServiceBuilder> serviceBuilders = new ArrayList<>();
        serviceBuilders.add(FavoritesService::new);
        serviceBuilders.add(FavCollectedService::new);
        serviceBuilders.add(BangumiService::new);

        for (ServiceBuilder serviceBuilder : serviceBuilders) {
            try {
                serviceBuilder.build(client, new User(uid), path).backup();
            } catch (BusinessException be) {
                log.info(be.getMessage());
            }
        }
    }

}
