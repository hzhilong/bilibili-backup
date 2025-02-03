package io.github.hzhilong.bilibili.backup.gui.segment;

/**
 * 分段处理的回调
 *
 * @author hzhilong
 * @version 1.0
 */
public interface SegmentCallback {
    void unfinished(SegmentConfig segmentConfig);

    void finished(SegmentConfig segmentConfig);
}
