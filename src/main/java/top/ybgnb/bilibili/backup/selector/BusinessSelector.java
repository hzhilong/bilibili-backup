package top.ybgnb.bilibili.backup.selector;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.error.BusinessException;
import top.ybgnb.bilibili.backup.service.BaseBusinessService;
import top.ybgnb.bilibili.backup.service.impl.BackupBusinessService;
import top.ybgnb.bilibili.backup.service.impl.ReadAllMsgBusinessService;
import top.ybgnb.bilibili.backup.service.impl.RestoreBusinessService;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dream
 */
@Slf4j
public class BusinessSelector {
    
    public static Map<BuType, BaseBusinessService> businessSelector = new HashMap<>();

    static  {
        BackupBusinessService backupBusinessService = new BackupBusinessService();
        businessSelector.put(backupBusinessService.getRequestType(),backupBusinessService);
        RestoreBusinessService restoreBusinessService = new RestoreBusinessService();
        businessSelector.put(restoreBusinessService.getRequestType(),restoreBusinessService);
        ReadAllMsgBusinessService readAllMsgBusinessService = new ReadAllMsgBusinessService();
        businessSelector.put(readAllMsgBusinessService.getRequestType(),readAllMsgBusinessService);
    }
    
    public static Object processBusiness(BuType buType,Object requestMsg) {
        try {
            log.info("================【" + buType.getFunctionCnName() + "工具】================");
            BaseBusinessService baseBusinessService = businessSelector.get(buType);
            return baseBusinessService.process(requestMsg);
        } catch (BusinessException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("内部异常：" + e);
        }
        return null;
    }

    
    
}
