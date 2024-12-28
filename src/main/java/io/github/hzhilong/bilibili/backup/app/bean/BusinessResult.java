package io.github.hzhilong.bilibili.backup.app.bean;

import io.github.hzhilong.base.bean.BuResult;
import io.github.hzhilong.bilibili.backup.app.business.IBusinessType;
import lombok.Data;

/**
 * 备份还原结果
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
public class BusinessResult<D> extends BuResult<D> {

    private IBusinessType businessType;

    private String itemName;

}
