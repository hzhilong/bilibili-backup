package io.github.hzhilong.bilibili.backup.gui.utils;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.FavFolder;
import io.github.hzhilong.bilibili.backup.api.bean.FavInfo;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavoritesService;
import io.github.hzhilong.bilibili.backup.gui.dialog.FavFolderSelectDialog;
import io.github.hzhilong.bilibili.backup.gui.dialog.FavInfoSelectDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 收藏夹工具
 *
 * @author hzhilong
 * @version 1.0
 */
public class FavFolderUtils {

    public static java.util.List<FavFolder> getFavFolders(FavoritesService service) throws BusinessException {
        java.util.List<FavFolder> favFolders = service.getFavFolders();
        if (ListUtil.isEmpty(favFolders)) {
            throw new BusinessException("当前账号无收藏夹（默认收藏夹咋也没了...）");
        }
        return favFolders;
    }

    public static java.util.List<FavInfo> getFavInfos(FavoritesService service, String uid) throws BusinessException {
        java.util.List<FavInfo> favInfos = service.getFavInfos(uid);
        if (ListUtil.isEmpty(favInfos)) {
            throw new BusinessException("该用户未公开任何收藏夹");
        }
        return favInfos;
    }

    public static FavInfo chooseFavInfo(Window parentWindow, String appIconPath, List<FavInfo> favInfos) throws BusinessException {
        return chooseFavInfo(parentWindow, appIconPath, favInfos, null);
    }

    public static FavInfo chooseFavInfo(Window parentWindow, String appIconPath, List<FavInfo> favInfos, String title) throws BusinessException {
        FavInfoSelectDialog dialog = new FavInfoSelectDialog(parentWindow, appIconPath, favInfos);
        if (StringUtils.notEmpty(title)) {
            dialog.setTitle(title);
        }
        dialog.setVisible(true);
        favInfos = dialog.getSelectedList();
        if (ListUtil.isEmpty(favInfos)) {
            throw new BusinessException("未选择收藏夹");
        }
        return favInfos.get(0);
    }

    public static FavInfo chooseFavInfo(Window parentWindow, String appIconPath, FavoritesService service, String uid) throws BusinessException {
        return chooseFavInfo(parentWindow, appIconPath, getFavInfos(service, uid), null);
    }

    public static FavInfo chooseFavInfo(Window parentWindow, String appIconPath, FavoritesService service, String title, String uid) throws BusinessException {
        return chooseFavInfo(parentWindow, appIconPath, getFavInfos(service, uid), title);
    }

    public static FavFolder chooseFavFolder(Window parentWindow, String appIconPath, java.util.List<FavFolder> favFolders) throws BusinessException {
        return chooseFavFolder(parentWindow, appIconPath, favFolders, "收藏至：");
    }

    public static FavFolder chooseFavFolder(Window parentWindow, String appIconPath, java.util.List<FavFolder> favFolders, String title) throws BusinessException {
        FavFolderSelectDialog dialog = new FavFolderSelectDialog(parentWindow, appIconPath, favFolders, true);
        if (StringUtils.notEmpty(title)) {
            dialog.setTitle(title);
        }
        dialog.setVisible(true);
        favFolders = dialog.getSelectedList();
        if (ListUtil.isEmpty(favFolders)) {
            throw new BusinessException("未选择收藏夹");
        }
        return favFolders.get(0);
    }

    public static FavFolder chooseFavFolder(Window parentWindow, String appIconPath, FavoritesService service, int neededSpace) throws BusinessException {
        return chooseFavFolder(parentWindow, appIconPath, service, neededSpace, "收藏至：");
    }

    public static FavFolder chooseFavFolder(Window parentWindow, String appIconPath, FavoritesService service, int neededSpace, String title) throws BusinessException {
        List<FavFolder> favFolders = getFavFolders(service);
        FavFolder tarFav = chooseFavFolder(parentWindow, appIconPath, favFolders, title);
        if (tarFav.getRemainingCount() >= neededSpace) {
            return tarFav;
        } else {
            int result = JOptionPane.showConfirmDialog(parentWindow, "当前收藏夹剩余空间不够，是否继续？（后续可切换收藏夹）", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                return tarFav;
            } else {
                throw new BusinessException("操作已取消");
            }
        }
    }

    public static boolean isFavFull(ApiResult<?> apiResult) {
        // 无账号测试，暂时不知道具体错误代码和提示
        String msg = apiResult.getMessage();
        return msg.contains("上限") || msg.contains("已满");
    }

}
