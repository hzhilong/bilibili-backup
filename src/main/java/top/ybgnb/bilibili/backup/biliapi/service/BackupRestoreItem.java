package top.ybgnb.bilibili.backup.biliapi.service;

import top.ybgnb.bilibili.backup.biliapi.service.impl.BangumiService;
import top.ybgnb.bilibili.backup.biliapi.service.impl.BlackService;
import top.ybgnb.bilibili.backup.biliapi.service.impl.FavCollectedService;
import top.ybgnb.bilibili.backup.biliapi.service.impl.FavOpusesService;
import top.ybgnb.bilibili.backup.biliapi.service.impl.FavoritesService;
import top.ybgnb.bilibili.backup.biliapi.service.impl.FollowingService;
import top.ybgnb.bilibili.backup.biliapi.service.impl.ToViewService;

/**
 * @ClassName BackupRestoreItem
 * @Description 备份/还原项
 * @Author hzhilong
 * @Time 2024/11/29
 * @Version 1.0
 */
public enum BackupRestoreItem {
    FOLLOWING("关注", FollowingService::new),
    TO_VIEW("稍后再看", ToViewService::new),
    BLACK("黑名单", BlackService::new),
    FAVORITES("收藏夹", FavoritesService::new),
    FAV_OPUSES("收藏的专栏", FavOpusesService::new),
    BANGUMI("我的追番/追剧", BangumiService::new),
    FAV_COLLECTED("收藏的视频合集", FavCollectedService::new),
    ;

    /**
     * 服务名称
     */
    private final String name;

    /**
     * 服务执行类
     */
    private final ServiceBuilder serviceBuilder;

    BackupRestoreItem(String name, ServiceBuilder serviceBuilder) {
        this.name = name;
        this.serviceBuilder = serviceBuilder;
    }

    public String getName() {
        return name;
    }

    public ServiceBuilder getServiceBuilder() {
        return serviceBuilder;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
