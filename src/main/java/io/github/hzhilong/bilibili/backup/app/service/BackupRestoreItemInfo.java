package io.github.hzhilong.bilibili.backup.app.service;

import io.github.hzhilong.base.error.BusinessException;

import java.io.File;

/**
 * 备份还原项目信息
 *
 * @author hzhilong
 * @version 1.0
 */
public interface BackupRestoreItemInfo {

    /**
     * 获取备份成功的内容数
     */
    int getBackupCount(File dir) throws BusinessException;

}
