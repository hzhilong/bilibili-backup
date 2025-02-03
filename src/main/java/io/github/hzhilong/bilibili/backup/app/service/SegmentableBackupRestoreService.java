package io.github.hzhilong.bilibili.backup.app.service;

import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.gui.segment.SegmentCallback;
import io.github.hzhilong.bilibili.backup.gui.segment.SegmentConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

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

    private SegmentCallback segmentCallBack;
    private SegmentConfig segmentConfig;

    public SegmentableBackupRestoreService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    protected int getSegmentPageNo() {
        return segmentConfig == null ? 1 : segmentConfig.getNextPage();
    }

    protected int getSegmentMaxSize() {
        return segmentConfig == null ? -1 : segmentConfig.getMaxSize();
    }

    public void callbackBackupSegment(PageApi<?, ?> pageApi, List<?> data) {
        if (segmentConfig == null) {
            return;
        }
        segmentConfig.callback(segmentCallBack, segmentConfig.getNextPage(), pageApi.getPage(), pageApi.getPageSize(), ListUtil.getSize(data));
    }

    public void callbackRestoreSegment(List<?> data) {
        if (segmentConfig == null) {
            return;
        }
        segmentConfig.callback(segmentCallBack, segmentConfig.getNextPage(), segmentConfig.getNextPage(), segmentConfig.getMaxSize(), ListUtil.getSize(data));
    }

}
