package io.github.hzhilong.bilibili.backup.app.bean;

import io.github.hzhilong.base.bean.BuResult;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import lombok.Data;

/**
 * 备份还原结果
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
public class BackupRestoreResult<D> extends BuResult<D> {

    private BusinessType businessType;

    private String itemName;

}
