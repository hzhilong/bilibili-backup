package io.github.hzhilong.bilibili.backup.app.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.hzhilong.bilibili.backup.api.service.BackupRestoreItem;

/**
 * 备份文件
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackupFile {

    /**
     * 备份项目
     */
    private BackupRestoreItem item;

    /**
     * 备份内容数量
     * file为目录是，则计算目录下的文件数
     */
    private int count;

}
