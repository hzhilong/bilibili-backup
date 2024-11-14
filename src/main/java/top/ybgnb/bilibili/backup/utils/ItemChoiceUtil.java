package top.ybgnb.bilibili.backup.utils;

import lombok.extern.slf4j.Slf4j;
import top.ybgnb.bilibili.backup.constant.BuType;
import top.ybgnb.bilibili.backup.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static top.ybgnb.bilibili.backup.utils.CommonUtil.scannerThreadLocal;

/**
 * @author Dream
 */
@Slf4j
public class ItemChoiceUtil {

    public static List<ServiceBuilder> getServices() {
        Scanner sc = scannerThreadLocal.get();
        List<ServiceBuilder> builders = new ArrayList<>();
        chooseItem(sc, builders, "关注", FollowingService::new);
        chooseItem(sc, builders, "我的追番/追剧", BangumiService::new);
        chooseItem(sc, builders, "收藏的专栏", FavOpusesService::new);
        chooseItem(sc, builders, "收藏的视频合集", FavCollectedService::new);
        chooseItem(sc, builders, "稍后再看", ToViewService::new);
        chooseItem(sc, builders, "收藏夹", FavoritesService::new);
        chooseItem(sc, builders, "黑名单", BlackService::new);
        return builders;
    }

    private static void chooseItem(Scanner sc, List<ServiceBuilder> builders, String name, ServiceBuilder builder) {
        log.info("是否包括[{}]？", name);
        log.info("输入 y:是\t其他:否");
        String nextLine = sc.nextLine();
        if ("Y".equals(nextLine) || "y".equals(nextLine)) {
            builders.add(builder);
        }
    }
    
}
