package io.github.hzhilong.bilibili.backup.app.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.base.utils.ListUtil;

import java.util.List;

/**
 * 可分段处理的服务
 *
 * @author hzhilong
 * @version 1.0
 */
@Setter
@Getter
@Slf4j
public abstract class SegmentableBackupRestoreService<T> extends BackupRestoreService<T> {

    /**
     * 分段的开始页码
     */
    private int segmentBackupPageNo = 1;

    /**
     * 分段的内容最大数
     */
    private int segmentBackupMaxSize = -1;

    /**
     * 分段的开始页码
     */
    private int segmentRestorePageNo = 1;

    /**
     * 分段的内容最大数
     */
    private int segmentRestoreMaxSize = -1;

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
