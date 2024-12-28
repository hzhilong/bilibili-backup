package io.github.hzhilong.bilibili.backup.app.service;

import io.github.hzhilong.bilibili.backup.app.bean.ServiceBuilder;
import io.github.hzhilong.bilibili.backup.app.service.impl.BangumiService;
import io.github.hzhilong.bilibili.backup.app.service.impl.BlackService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavCollectedService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavOpusesService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavoritesService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FollowerService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FollowingService;
import io.github.hzhilong.bilibili.backup.app.service.impl.HistoryService;
import io.github.hzhilong.bilibili.backup.app.service.impl.ToViewService;

/**
 * 备份还原项
 *
 * @author hzhilong
 * @version 1.0
 */
public enum BackupRestoreItem {
    FOLLOWING("关注", FollowingService::new),
    TO_VIEW("稍后再看", ToViewService::new),
    BLACK("黑名单", BlackService::new),
    FAVORITES("收藏夹", FavoritesService::new),
    FAV_OPUSES("收藏的专栏", FavOpusesService::new),
    BANGUMI("我的追番/追剧", BangumiService::new),
    FAV_COLLECTED("收藏的视频合集", FavCollectedService::new),
    HISTORY("历史记录", HistoryService::new),
    FOLLOWER("粉丝", FollowerService::new);

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
