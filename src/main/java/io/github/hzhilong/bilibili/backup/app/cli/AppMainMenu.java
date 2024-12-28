package io.github.hzhilong.bilibili.backup.app.cli;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.cli.business.BusinessRunner;
import io.github.hzhilong.bilibili.backup.app.cli.business.CLIBusinessType;
import io.github.hzhilong.bilibili.backup.app.utils.MenuUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * app主菜单
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class AppMainMenu extends BaseMenu {

    /**
     * 选择业务
     */
    public static void chooseBusiness(Scanner sc) {
        do {
            log.info("请选择对应功能的序号:");
            CLIBusinessType[] allTypes = CLIBusinessType.values();
            for (int i = 0; i < allTypes.length; i++) {
                log.info("{}: {}", i, allTypes[i].getName());
            }
            log.info("\n");

            int pos = MenuUtil.checkInputPos(allTypes.length, sc.nextLine());
            // 输入是否有效
            if (pos > -1) {
                // 判断是否退出
                CLIBusinessType businessType = allTypes[pos];
                if (CLIBusinessType.EXIT.equals(businessType)) {
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
