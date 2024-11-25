package top.ybgnb.bilibili.backup.app.business.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.app.business.BaseBusiness;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;
import top.ybgnb.bilibili.backup.app.menu.BaseMenu;
import top.ybgnb.bilibili.backup.app.menu.UserMenu;
import top.ybgnb.bilibili.backup.app.menu.btn.callback.YesOrNo;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;
import top.ybgnb.bilibili.backup.biliapi.bean.Video;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.AddQueryParams;
import top.ybgnb.bilibili.backup.biliapi.request.BaseApi;
import top.ybgnb.bilibili.backup.biliapi.service.ServiceBuilder;
import top.ybgnb.bilibili.backup.biliapi.service.impl.BangumiService;
import top.ybgnb.bilibili.backup.biliapi.service.impl.FavCollectedService;
import top.ybgnb.bilibili.backup.biliapi.service.impl.FavoritesService;
import top.ybgnb.bilibili.backup.biliapi.service.impl.VideoService;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.FileUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @ClassName CancelledAccountsBusiness
 * @Description 已注销账号数据
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
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
                    getUserVideo(uid, path);
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
                backupInfo(uid, path);
                upper[0] = new Upper(Long.valueOf(uid), AppConstant.CANCELLED_ACCOUNT_NAME, "");
            }

            @Override
            public void no() {

            }
        });
        return upper[0];
    }

    private void getUserInfo(String uid) throws BusinessException {
        ApiResult<JSONObject> apiResult = new BaseApi<JSONObject>(client, null, "https://api.bilibili.com/x/relation/stat", new AddQueryParams() {
            @Override
            public void addQueryParams(Map<String, String> queryParams) {
                queryParams.put("vmid", uid);
            }
        }, false, JSONObject.class).apiGet();
        if (apiResult._isSuccess()) {
            JSONObject data = apiResult.getData();
            log.info("");
            log.info("用户UID：{}", uid);
            log.info("该用户关注数：{}", data.getInteger("following"));
            log.info("该用户粉丝数：{}", data.getInteger("follower"));
        } else {
            throw new BusinessException("查询该uid用户信息失败");
        }
    }

    private void getUserVideo(String uid, String path) throws BusinessException {
        log.info("正在获取该用户投稿的视频，请稍候...");
        List<Video> videos = new VideoService(client, new User(uid)).getVideos(uid);
        log.info("该用户共投稿{}个视频。", videos.size());
        for (int i = 0; i < videos.size(); i++) {
            Video video = videos.get(i);
            log.info("{}.{} {}", i + 1, video.getBvid(), video.getTitle());
        }
        FileUtil.writeJsonFile(path, "投稿的视频.json", videos);
    }

    private void backupInfo(String uid, String path) {
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
