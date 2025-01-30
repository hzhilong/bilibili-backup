package io.github.hzhilong.bilibili.backup.app.state.setting;

import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.business.IBusinessType;
import io.github.hzhilong.baseapp.constant.BaseAppConstant;
import io.github.hzhilong.baseapp.state.data.BaseAppData;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.gui.segment.SegmentConfig;

/**
 * APP数据
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppData extends BaseAppData {

    public AppData(String appDataPath) {
        super(appDataPath);
    }

    private static class Base {
        private final static AppData INSTANCE = new AppData(BaseAppConstant.APP_DATA_PATH);
    }

    public static AppData getInstance() {
        return Base.INSTANCE;
    }

    /**
     * 获取存在的分段处理配置
     */
    public SegmentConfig getSegmentConfig(String uid, IBusinessType businessType, BackupRestoreItem backupRestoreItem) {
        String property = read(SegmentConfig.getAppPropertyKey(uid, businessType, backupRestoreItem));
        if (StringUtils.isEmpty(property)) {
            return null;
        }
        return SegmentConfig.parse(uid, businessType, backupRestoreItem, property);
    }

    public void setSegmentConfig(SegmentConfig segmentConfig) {
        write(segmentConfig.getAppPropertyKey(), segmentConfig.getAppPropertyValue());
        persistent();
    }

    public void delSegmentConfig(SegmentConfig segmentConfig) {
        remove(segmentConfig.getAppPropertyKey());
        persistent();
    }

}
