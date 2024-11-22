package top.ybgnb.bilibili.backup.biliapi.service;

import top.ybgnb.bilibili.backup.biliapi.bean.Upper;
import top.ybgnb.bilibili.backup.app.business.BusinessType;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;

public interface BaseBusinessService {

    Upper process(Object requestMsg) throws BusinessException;
    
    BusinessType getBuType();
}
