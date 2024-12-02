package top.ybgnb.bilibili.backup.biliapi.service;

import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;

import java.io.File;

/**
 * @ClassName BackupRestoreItemInfo
 * @Description 备份还原项目信息
 * @Author hzhilong
 * @Time 2024/11/29
 * @Version 1.0
 */
public interface BackupRestoreItemInfo {

    /**
     * 获取备份成功的内容数
     *
     * @param dir
     * @return
     */
    int getBackupCount(File dir) throws BusinessException;

}
