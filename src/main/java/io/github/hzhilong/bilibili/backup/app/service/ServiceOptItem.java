package io.github.hzhilong.bilibili.backup.app.service;

import io.github.hzhilong.baseapp.bean.OptItem;
import io.github.hzhilong.bilibili.backup.app.bean.ServiceBuilder;

/**
 * 服务操作项
 *
 * @author hzhilong
 * @version 1.0
 */
public interface ServiceOptItem extends OptItem {

    /**
     * 获取服务执行类
     *
     * @return 服务执行类的构建
     */
    ServiceBuilder getServiceBuilder();
}
