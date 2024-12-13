package io.github.hzhilong.bilibili.backup.app.cli;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.app.business.BusinessRunner;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.utils.MenuUtil;
import io.github.hzhilong.base.error.BusinessException;

import java.util.Scanner;

/**
 * app主菜单
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class AppMainMenu extends BaseMenu{

    /**
     * 选择业务
     */
    public static void chooseBusiness(Scanner sc) {
        do {
            log.info("请选择对应功能的序号:");
            BusinessType[] businessTypes = BusinessType.values();
            for (int i = 0; i < businessTypes.length; i++) {
                log.info("{}: {}", i, businessTypes[i].getName());
            }
            log.info("\n");

            int pos = MenuUtil.checkInputPos(businessTypes.length, sc.nextLine());
            // 输入是否有效
            if (pos > -1) {
                // 判断是否退出
                BusinessType businessType = businessTypes[pos];
                if (BusinessType.EXIT.equals(businessType)) {
                    return;
                }
                // 执行业务
                try {
                    BusinessRunner.processBusiness(businessType, sc);
                } catch (BusinessException ignored) {

                }
            }
        } while (true);
    }


}
