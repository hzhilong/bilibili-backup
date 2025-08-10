package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.baseapp.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.api.bean.FavFolder;
import io.github.hzhilong.bilibili.backup.api.bean.FavInfo;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavoritesService;
import io.github.hzhilong.bilibili.backup.app.utils.PageUtils;
import io.github.hzhilong.bilibili.backup.gui.dialog.PageInputDialog;
import io.github.hzhilong.bilibili.backup.gui.utils.CommonDialog;
import io.github.hzhilong.bilibili.backup.gui.utils.FavFolderUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.awt.*;
import java.util.LinkedHashSet;

/**
 * 拷贝收藏夹
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class CopyFavRunnable extends ToolRunnable<FavoritesService, Void> implements NeedContext {

    private FavoritesService favoritesService;

    @Setter
    private Window parentWindow;
    @Setter
    private String appIconPath;

    public CopyFavRunnable(OkHttpClient client, SavedUser user, ToolBuCallback<Void> buCallback) {
        super(client, user, buCallback);
    }

    @Override
    protected void newServices(LinkedHashSet<FavoritesService> services) {
        favoritesService = new FavoritesService(client, new User(user.getCookie()), "");
        services.add(favoritesService);
    }

    @Override
    protected Void runTool() throws BusinessException {
        // 输入uid
        String uid = CommonDialog.getUid(parentWindow);
        // 获取收藏夹
        log.info("获取uid {}的收藏夹中...", uid);
        FavInfo srcFav = FavFolderUtils.chooseFavInfo(parentWindow, appIconPath, favoritesService, uid);
        Integer mediaCount = srcFav.getMediaCount();
        if (mediaCount < 1) {
            throw new BusinessException("该收藏夹为空");
        }

        // 输入拷贝范围
        int start = 1;
        int end = (mediaCount - 1) / 40 + 1;
        PageInputDialog pageInputDialog = new PageInputDialog(parentWindow, appIconPath, "提示", "请输入需要拷贝的范围，默认拷贝全部", start, end);
        pageInputDialog.setVisible(true);
        int[] pages = pageInputDialog.getResult();
        if (pages[0] == -1) {
            throw new BusinessException("取消操作");
        }

        log.info("拷贝范围{}-{}", pages[0], pages[1]);
        int selectCount = PageUtils.getDataCountInPageRange(mediaCount, 40, pages[0], pages[1]);
        log.info("该范围共有{}个视频", selectCount);
        log.info("获取当前账号的收藏夹中...");
        FavFolder tarFav = FavFolderUtils.chooseFavFolder(parentWindow, appIconPath, favoritesService, selectCount);

        log.info("即将开始拷贝收藏夹 {}=>{}", srcFav.getTitle(), tarFav.getTitle());
        favoritesService.copy(uid, String.valueOf(srcFav.getId()), String.valueOf(tarFav.getId()), pages[0], pages[1], true);
        return null;
    }


}
