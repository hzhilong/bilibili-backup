package io.github.hzhilong.bilibili.backup.gui.segment;

import io.github.hzhilong.baseapp.business.IBusinessType;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 分段处理的配置
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SegmentConfig {

    public static final String SEPARATOR = "@";

    private IBusinessType businessType;

    private String uid;

    private BackupRestoreItem backupRestoreItem;

    private String path;

    private int nextPage;


    public static String getAppPropertyKey(String uid, IBusinessType businessType, BackupRestoreItem backupRestoreItem) {
        return uid + SEPARATOR + businessType + SEPARATOR + backupRestoreItem.getName();
    }

    public String getAppPropertyKey() {
        return uid + SEPARATOR + businessType + SEPARATOR + backupRestoreItem.getName();
    }

    public String getAppPropertyValue() {
        return path + SEPARATOR + nextPage;
    }

    public static SegmentConfig parse(String uid, IBusinessType businessType, BackupRestoreItem backupRestoreItem, String appPropertyValue) {
        SegmentConfig segmentConfig = new SegmentConfig();
        segmentConfig.setUid(uid);
        segmentConfig.setBusinessType(businessType);
        segmentConfig.setBackupRestoreItem(backupRestoreItem);
        String[] strings = appPropertyValue.split(SEPARATOR);
        segmentConfig.setPath(strings[0]);
        segmentConfig.setNextPage(Integer.parseInt(strings[1]));
        return segmentConfig;
    }

}
