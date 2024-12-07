package top.ybgnb.bilibili.backup.biliapi.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;

import java.util.List;

/**
 * @ClassName SegmentableBackupRestoreService
 * @Description 可分段处理的服务
 * @Author hzhilong
 * @Time 2024/12/6
 * @Version 1.0
 */
@Slf4j
public abstract class SegmentableBackupRestoreService extends BackupRestoreService {

    /**
     * 分段的开始页码
     */
    @Setter
    @Getter
    private int segmentBackupPageNo = 1;

    /**
     * 分段的内容最大数
     */
    @Setter
    @Getter
    private int segmentBackupMaxSize = -1;

    /**
     * 分段的开始页码
     */
    @Setter
    @Getter
    private int segmentRestorePageNo = 1;

    /**
     * 分段的内容最大数
     */
    @Setter
    @Getter
    private int segmentRestoreMaxSize = -1;

    @Getter
    @Setter
    private SegmentCallback segmentCallBack;


    public SegmentableBackupRestoreService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    public interface SegmentCallback {
        void unfinished(int currPage, int currPageSize, int currSize);

        void finished(int currPage, int currPageSize, int currSize);
    }

    public void callbackSegment(int startPage, int currPage, int currPageSize, int currSize) {
        if (segmentCallBack != null) {
            if (currSize < currPageSize || currSize < (currPage - startPage + 1) * currPageSize || currSize <= 0) {
                log.info("当前分段处理已全部完成，页码：{}", currPage);
                segmentCallBack.finished(currPage, currPageSize, currSize);
            } else {
                log.info("当前分段处理完成，页码：{}", currPage);
                segmentCallBack.unfinished(currPage, currPageSize, currSize);
            }
        }
    }

    public void callbackBackupSegment(PageApi<?, ?> pageApi, List<?> data) {
        callbackSegment(getSegmentBackupPageNo(), pageApi.getPage(), pageApi.getPageSize(), ListUtil.getSize(data));
    }

    public void callbackRestoreSegment(List<?> data) {
        callbackSegment(getSegmentRestorePageNo(), getSegmentRestorePageNo(), getSegmentRestoreMaxSize(), ListUtil.getSize(data));
    }

}
