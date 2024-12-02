package top.ybgnb.bilibili.backup.ui.component;

import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;

/**
 * @ClassName ComponentInit
 * @Description 组件初始化（主动调用）
 * @Author hzhilong
 * @Time 2024/11/27
 * @Version 1.0
 */
public interface ComponentInit {

    default void init() throws BusinessException {
        initData();
        initUI();
    }

    void initData() throws BusinessException;

    void initUI() throws BusinessException;

}
