package top.ybgnb.bilibili.backup.ui.worker;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;
import top.ybgnb.bilibili.backup.biliapi.bean.Video;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.impl.VideoService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UpperVideosRunnable
 * @Description 获取UP投稿视频的线程
 * @Author hzhilong
 * @Time 2024/11/28
 * @Version 1.0
 */
@Slf4j
public class UpperVideosRunnable extends BaseRunnable {

    private String uid;

    private BuCallback<List<Video>> buCallback;

    public UpperVideosRunnable(OkHttpClient client, String uid, BuCallback<List<Video>> buCallback) {
        super(client);
        this.uid = uid;
        this.buCallback = buCallback;
    }

    @Override
    public void run() {
        boolean onceSuccessful = false;
        List<Video> result = null;
        try {
            VideoService videoService = new VideoService(client, new User(uid));
            result = videoService.getVideos(uid);
            // 设置当前备份目录
            String path = String.format("%s%s_%s_%s/", AppConstant.BACKUP_PATH_PREFIX, AppConstant.CANCELLED_ACCOUNT_NAME, uid,
                    new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
            try {
                videoService.backup(path, result);
            } catch (BusinessException e) {
                log.info("保存备份文件失败：{}", e.getMessage());
            }
            onceSuccessful = true;
        } catch (BusinessException e) {
            log.info("操作失败，{}\n", e.getMessage());
        } finally {
            if (buCallback != null) {
                if (interrupt) {
                    log.info("已中断任务");
                    buCallback.interrupt();
                } else {
                    if (onceSuccessful) {
                        log.info("操作成功！");
                        buCallback.success(result);
                    } else {
                        log.info("操作失败！");
                        buCallback.fail("操作失败！");
                    }
                }
            }
        }
    }

}
