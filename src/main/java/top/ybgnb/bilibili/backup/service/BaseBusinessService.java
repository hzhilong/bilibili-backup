package top.ybgnb.bilibili.backup.service;

import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.error.BusinessException;

public interface BaseBusinessService {
    
    Object process(Object requestMsg) throws BusinessException;
    
    BuType getRequestType();
}
