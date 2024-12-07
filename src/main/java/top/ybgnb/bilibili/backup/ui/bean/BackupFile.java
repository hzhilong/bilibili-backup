package top.ybgnb.bilibili.backup.ui.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.ybgnb.bilibili.backup.biliapi.service.BackupRestoreItem;

/**
 * @ClassName BackupFile
 * @Description 备份文件
 * @Author hzhilong
 * @Time 2024/11/29
 * @Version 1.0
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
