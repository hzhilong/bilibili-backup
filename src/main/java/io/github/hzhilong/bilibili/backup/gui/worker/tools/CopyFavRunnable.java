package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.api.bean.FavFolder;
import io.github.hzhilong.bilibili.backup.api.bean.FavInfo;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavoritesService;
import io.github.hzhilong.bilibili.backup.gui.dialog.FavFolderSelectDialog;
import io.github.hzhilong.bilibili.backup.gui.dialog.FavInfoSelectDialog;
import io.github.hzhilong.bilibili.backup.gui.dialog.PageInputDialog;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;

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
        String uid = getUid();
        // 获取收藏夹
        log.info("获取uid {}的收藏夹中...", uid);
        List<FavInfo> favInfos = getFavInfos(uid);
        handleInterrupt();
        // 选择收藏夹
        FavInfo srcFav = chooseFavInfo(favInfos);
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
        int selectCount = getDataCountInPageRange(mediaCount, 40, pages[0], pages[1]);
        log.info("该范围共有{}个视频", selectCount);
        log.info("获取当前账号的收藏夹中...");
        List<FavFolder> favFolders = getFavFolders();
        FavInfo tarFav;
        while (true) {
            tarFav = chooseFavFolder(favFolders);
            if (tarFav.getRemainingCount() < selectCount) {
                JOptionPane.showMessageDialog(parentWindow, "当前收藏夹剩余空间不够", "提示", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        log.info("即将开始拷贝收藏夹 {}=>{}", srcFav.getTitle(), tarFav.getTitle());
        favoritesService.copy(uid, String.valueOf(srcFav.getId()), String.valueOf(tarFav.getId()), pages[0], pages[1]);
        return null;
    }

    private String getUid() throws BusinessException {
        String uid = JOptionPane.showInputDialog(parentWindow, "请输入公开收藏夹的用户UID：",
                "提示", JOptionPane.QUESTION_MESSAGE);
        if (StringUtils.isEmpty(uid)) {
            JOptionPane.showMessageDialog(parentWindow, "请输入用户UID！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("请输入用户UID！");
        } else if (!AppConstant.NUM_PATTERN.matcher(uid).find()) {
            JOptionPane.showMessageDialog(parentWindow, "用户UID为纯数字！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("用户UID为纯数字！");
        }
        return uid;
    }

    private java.util.List<FavInfo> getFavInfos(String uid) throws BusinessException {
        java.util.List<FavInfo> favInfos = favoritesService.getFavInfos(uid);
        if (ListUtil.isEmpty(favInfos)) {
            throw new BusinessException("该用户未公开任何收藏夹");
        }
        return favInfos;
    }

    private FavInfo chooseFavInfo(List<FavInfo> favInfos) throws BusinessException {
        FavInfoSelectDialog dialog = new FavInfoSelectDialog(parentWindow, appIconPath, favInfos);
        dialog.setVisible(true);
        favInfos = dialog.getSelectedList();
        if (ListUtil.isEmpty(favInfos)) {
            throw new BusinessException("未选择收藏夹");
        }
        return favInfos.get(0);
    }

    private java.util.List<FavFolder> getFavFolders() throws BusinessException {
        java.util.List<FavFolder> favFolders = favoritesService.getFavFolders();
        if (ListUtil.isEmpty(favFolders)) {
            throw new BusinessException("当前账号无收藏夹（默认收藏夹咋也没了...）");
        }
        return favFolders;
    }

    private FavFolder chooseFavFolder(List<FavFolder> favFolders) throws BusinessException {
        FavFolderSelectDialog dialog = new FavFolderSelectDialog(parentWindow, appIconPath, favFolders, true);
        dialog.setTitle("复制至：");
        dialog.setVisible(true);
        favFolders = dialog.getSelectedList();
        if (ListUtil.isEmpty(favFolders)) {
            throw new BusinessException("未选择收藏夹");
        }
        return favFolders.get(0);
    }

    public static int getDataCountInPageRange(int total, int pageSize, int startPage, int endPage) {
        if (pageSize <= 0 || total <= 0) return 0;

        int maxPage = (int) Math.ceil((double) total / pageSize);

        // 修正页码范围
        startPage = Math.max(1, startPage);
        endPage = Math.min(endPage, maxPage);
        if (startPage > endPage) return 0;

        int startIndex = (startPage - 1) * pageSize;
        int endIndex = Math.min(endPage * pageSize, total);

        return endIndex - startIndex;
    }

}
