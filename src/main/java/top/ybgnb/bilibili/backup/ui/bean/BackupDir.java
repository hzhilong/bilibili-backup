package top.ybgnb.bilibili.backup.ui.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * @ClassName BackupDir
 * @Description 备份目录
 * @Author hzhilong
 * @Time 2024/11/29
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackupDir {

    /**
     * 备份名称(昵称+uid+日期时间)
     */
    private String name;

    /**
     * 备份目录
     */
    private File dirFile;

    /**
     * 备份的文件
     */
    private List<BackupFile> backupFiles;

    @Override
    public String toString() {
        return name;
    }

}
