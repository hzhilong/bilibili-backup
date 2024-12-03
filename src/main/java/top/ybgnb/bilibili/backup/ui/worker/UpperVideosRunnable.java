package top.ybgnb.bilibili.backup.ui.worker;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.Video;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.service.impl.VideoService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

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
            result = new VideoService(client, new User(uid)).getVideos(uid);
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