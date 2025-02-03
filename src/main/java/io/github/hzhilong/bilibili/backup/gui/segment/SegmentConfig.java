package io.github.hzhilong.bilibili.backup.gui.segment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.business.IBusinessType;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 分段处理的配置
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@AllArgsConstructor
@Slf4j
public class SegmentConfig {

    /**
     * 存储时的key分隔符
     */
    public static final String SEPARATOR = "@";

    /**
     * 业务类型
     */
    private BusinessType businessType;

    /**
     * 用户uid
     */
    private String uid;

    /**
     * 操作项目
     */
    private BackupRestoreItem backupRestoreItem;

    /**
     * 数据路径
     */
    private String path;

    /**
     * 下一页
     */
    private int nextPage = 1;

    /**
     * 分段的数量
     */
    private int maxSize = -1;

    public SegmentConfig() {
    }

    public SegmentConfig(String uid, BusinessType businessType, BackupRestoreItem backupRestoreItem, String backupDirPath, int maxSize) {
        this(businessType, uid, backupRestoreItem, backupDirPath, 1, maxSize);
    }

    /**
     * 获取持久化数据的key
     *
     * @param uid
     * @param businessType
     * @param backupRestoreItem
     * @return
     */
    @JSONField(serialize = false)
    public static String getPersistenceDataKey(String uid, IBusinessType businessType, BackupRestoreItem backupRestoreItem) {
        return uid + SEPARATOR + businessType + SEPARATOR + backupRestoreItem.getName();
    }

    /**
     * 获取持久化数据的key
     *
     * @return
     */
    @JSONField(serialize = false)
    public String getPersistenceDataKey() {
        return getPersistenceDataKey(uid, businessType, backupRestoreItem);
    }

    /**
     * 获取持久化数据的value
     *
     * @return
     */
    @JSONField(serialize = false)
    public String getPersistenceDataValue() {
        return JSON.toJSONString(this);
    }

    public static SegmentConfig parsePersistenceData(String persistenceData) {
        if (StringUtils.isEmpty(persistenceData)) {
            return null;
        }
        try {
            return JSON.parseObject(persistenceData, SegmentConfig.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void callback(SegmentCallback callback, int startPage, int currPage, int currPageSize, int currSize) {
        if (callback != null) {
            if (currSize < currPageSize || currSize < (currPage - startPage + 1) * currPageSize || currSize <= 0) {
                log.info("当前分段处理已全部完成，页码：{}", currPage);
                callback.finished(this);
            } else {
                log.info("当前分段处理完成，页码：{}", currPage);
                this.setNextPage(currPage + 1);
                callback.unfinished(this);
            }
        }
    }

}
