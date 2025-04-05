package io.github.hzhilong.bilibili.backup.gui.page;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.FavInfo;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavoritesService;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.gui.dialog.FavInfoSelectDialog;
import io.github.hzhilong.bilibili.backup.gui.dialog.PageInputDialog;
import io.github.hzhilong.bilibili.backup.gui.worker.DelaySetProcessingLoggerRunnable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * 已注销账号数据 页面
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class CopyFavPage extends PagePanel {

    private String cookie;

    private JTextField txtUid;

    private JButton btnCopyFav;

    private JTextArea txtLog;

    private boolean loading = false;

    private JFrame parent;

    private FavoritesService favoritesService;

    public CopyFavPage(JFrame parent, String appIconPath, OkHttpClient client, String cookie) {
        super(parent, appIconPath, client);
        this.cookie = cookie;
        this.parent = parent;
        favoritesService = new FavoritesService(client, new User(cookie), "");
    }

    @Override
    public void initData() {
    }

    @Override
    public void initUI() {
        int posY = 0;
        int posX = 0;
        JLabel jLabel = new JLabel("请输入 UID：");
        addFixedContent(jLabel, posX++, posY);

        txtUid = new JTextField("", 14);
        txtUid.setMinimumSize(new Dimension(100, 30));
        addFixedContent(txtUid, posX++, posY);

        btnCopyFav = new JButton("拷贝公开的收藏夹");
        addFixedContent(btnCopyFav, posX++, posY);

        posY++;

        JScrollPane scrollPaneLog = addTxtLogToDynamic(0, posY++, 2);
        txtLog = (JTextArea) scrollPaneLog.getViewport().getView();

        initListener();
    }

    private void initListener() {
        btnCopyFav.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loading) {
                    if (favoritesService != null) {
                        favoritesService.setInterrupt(true);
                    }
                    setBusyStatus(false);
                } else {
                    copyFav();
                }
            }
        });
    }

    private String getUid() throws BusinessException {
        String uid = txtUid.getText();
        if (GlobalState.getProcessing()) {
            JOptionPane.showMessageDialog(parentWindow, "有其他任务在运行！", "提示", JOptionPane.WARNING_MESSAGE);
            throw new BusinessException("有其他任务在运行！");
        } else if (StringUtils.isEmpty(uid)) {
            JOptionPane.showMessageDialog(parentWindow, "请输入用户UID！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("请输入用户UID！");
        } else if (!AppConstant.NUM_PATTERN.matcher(uid).find()) {
            JOptionPane.showMessageDialog(parentWindow, "用户UID为纯数字！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("用户UID为纯数字！");
        } else {
            return uid;
        }
    }

    private List<FavInfo> getFavInfos(String uid) throws BusinessException {
        List<FavInfo> favInfos = favoritesService.getFavInfos(uid);
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

    private void setBusyStatus(boolean flag) {
        loading = flag;
        if (loading) {
            btnCopyFav.setText("停止操作");
            GlobalState.setProcessingLogger(txtLog);
        } else {
            btnCopyFav.setText("拷贝公开的收藏夹");
            new Thread(new DelaySetProcessingLoggerRunnable(null)).start();
        }
        GlobalState.setProcessing(loading);
    }

    private void copyFav() {
        try {
            String uid = getUid();
            setBusyStatus(true);
            List<FavInfo> favInfos = getFavInfos(uid);
            if (!loading) {
                throw new BusinessException("取消操作");
            }
            FavInfo favInfo = chooseFavInfo(favInfos);
            Integer mediaCount = favInfo.getMediaCount();
            if (mediaCount < 1) {
                throw new BusinessException("该收藏夹为空");
            }

            int start = 1;
            int end = (mediaCount - 1) / 40 + 1;
            PageInputDialog pageInputDialog = new PageInputDialog(parentWindow, appIconPath, "提示", "请输入需要拷贝的范围，默认拷贝全部", start, end);
            pageInputDialog.setVisible(true);
            int[] pages = pageInputDialog.getResult();
            if (pages[0] == -1) {
                throw new BusinessException("取消操作");
            }
            System.out.println(Arrays.toString(pages));
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }


}