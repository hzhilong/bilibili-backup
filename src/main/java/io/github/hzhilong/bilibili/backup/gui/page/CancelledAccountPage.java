package io.github.hzhilong.bilibili.backup.gui.page;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.CancelledAccountInfo;
import io.github.hzhilong.bilibili.backup.api.bean.Upper;
import io.github.hzhilong.bilibili.backup.api.bean.Video;
import io.github.hzhilong.bilibili.backup.api.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.gui.component.PagePanel;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.gui.utils.LayoutUtil;
import io.github.hzhilong.bilibili.backup.gui.worker.BackupRestoreRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.BackupRunnable;
import io.github.hzhilong.base.bean.BuCallback;
import io.github.hzhilong.bilibili.backup.gui.worker.CancelledAccountInfoRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.DelaySetProcessingLoggerRunnable;
import io.github.hzhilong.bilibili.backup.gui.worker.UpperVideosRunnable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 已注销账号数据 页面
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class CancelledAccountPage extends PagePanel {

    public static final String ACTIVE_BACKUP_BTN_NAME = "备份数据（收藏夹、收藏的视频合集、追番追剧）";
    public static final String INACTIVE_BACKUP_BTN_NAME = "取消备份";

    private JTextField txtUid;

    private JButton btnGetInfo;

    private JButton btnGetVideos;

    private JButton btnBackup;

    private JTextArea txtLog;

    private CancelledAccountInfoRunnable infoRunnable;
    private UpperVideosRunnable upperVideosRunnable;
    private BackupRestoreRunnable backupRunnable;

    public CancelledAccountPage(OkHttpClient client) {
        super(client);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initUI() {
        int posY = 0;
        GridBagConstraints tempConstraints;

        JLabel jLabel = new JLabel("请输入 UID：");
        addFixedContent(jLabel, 0, posY);

        txtUid = new JTextField();
        txtUid.setPreferredSize((new Dimension(200, 30)));
        addFixedContent(txtUid, 1, posY++);

        addSeparatorToFixed(0, posY++, 2);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout());
        tempConstraints = LayoutUtil.getSeparatorConstraints(0, posY++, 2);
        addFixedContent(btnPanel, tempConstraints);

        btnGetInfo = new JButton("获取信息");
        btnGetVideos = new JButton("获取投稿");
        btnBackup = new JButton("备份数据（收藏夹、收藏的视频合集、追番追剧）");
        btnPanel.add(btnGetInfo);
        btnPanel.add(btnGetVideos);
        btnPanel.add(btnBackup);

        JScrollPane scrollPaneLog = addTxtLogToDynamic(0, posY++, 2);
        txtLog = (JTextArea) scrollPaneLog.getViewport().getView();

        initListener();
    }

    private void initListener() {
        btnGetInfo.addActionListener(e -> onBtnGetInfo());
        btnGetVideos.addActionListener(e -> onBtnGetVideos());
        btnBackup.addActionListener(e -> onBtnBackup());
    }

    private String getUid(int btnNo) throws BusinessException {
        String uid = txtUid.getText();
        if (GlobalState.getProcessing() && (btnNo != 3 || !isBackingUp())) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "有其他任务在运行！", "提示", JOptionPane.WARNING_MESSAGE);
            throw new BusinessException("有其他任务在运行！");
        } else if (StringUtils.isEmpty(uid)) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "请输入用户UID！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("请输入用户UID！");
        } else if (!AppConstant.NUM_PATTERN.matcher(uid).find()) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "用户UID为纯数字！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new BusinessException("用户UID为纯数字！");
        } else {
            return uid;
        }
    }

    private void onBtnGetInfo() {
        try {
            String uid = getUid(1);
            setBusyStatus(1, true);
            infoRunnable = new CancelledAccountInfoRunnable(client, uid, new BuCallback<CancelledAccountInfo>() {
                @Override
                public void success(CancelledAccountInfo info) {
                    log.info("\n用户UID：{}", info.getUid());
                    log.info("该用户关注数：{}", info.getFollowingCount());
                    log.info("该用户粉丝数：{}", info.getFollowerCount());
                    setBusyStatus(1, false);
                }

                @Override
                public void fail(String msg) {
                    setBusyStatus(1, false);
                }

                @Override
                public void interrupt() {
                    setBusyStatus(1, false);
                }
            });
            new Thread(infoRunnable).start();
        } catch (BusinessException ignored) {
        }
    }

    private void onBtnGetVideos() {
        try {
            String uid = getUid(2);
            setBusyStatus(2, true);
            upperVideosRunnable = new UpperVideosRunnable(client, uid, new BuCallback<List<Video>>() {
                @Override
                public void success(List<Video> videos) {
                    log.info("\n该用户共投稿{}个视频。", videos.size());
                    for (int i = 0; i < videos.size(); i++) {
                        Video video = videos.get(i);
                        log.info("{}.{} {}", i + 1, video.getBvid(), video.getTitle());
                    }
                    setBusyStatus(1, false);
                }

                @Override
                public void fail(String msg) {
                    setBusyStatus(2, false);
                }

                @Override
                public void interrupt() {
                    setBusyStatus(2, false);
                }
            });
            new Thread(upperVideosRunnable).start();
        } catch (BusinessException ignored) {
        }
    }

    private void onBtnBackup() {
        try {
            String uid = getUid(3);
            if (!isBackingUp()) {
                int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                        "是否开始备份？", "提示", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    backup(uid);
                }
            } else {
                int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                        "正在进行备份，是否取消？", "提示", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    stopBackup();
                }
            }
        } catch (BusinessException ignored) {
        }
    }

    private boolean isBackingUp() {
        return INACTIVE_BACKUP_BTN_NAME.equals(this.btnBackup.getText());
    }

    private void setBusyStatus(int btnNo, boolean flag) {
        if (btnNo == 3) {
            if (flag) {
                this.btnBackup.setText(INACTIVE_BACKUP_BTN_NAME);
            } else {
                this.btnBackup.setText(ACTIVE_BACKUP_BTN_NAME);
            }
        } else {
            btnBackup.setEnabled(!flag);
        }
        btnGetInfo.setEnabled(!flag);
        btnGetVideos.setEnabled(!flag);
        if (flag) {
            GlobalState.setProcessingLogger(txtLog);
        } else {
            new Thread(new DelaySetProcessingLoggerRunnable(null)).start();
        }
        GlobalState.setProcessing(flag);
    }

    private void backup(String uid) {
        setBusyStatus(3, true);
        LinkedHashSet<BackupRestoreItem> items = new LinkedHashSet<>();
        items.add(BackupRestoreItem.FAVORITES);
        items.add(BackupRestoreItem.FAV_COLLECTED);
        items.add(BackupRestoreItem.BANGUMI);

        SavedUser user = new SavedUser(new Upper(Long.valueOf(uid), AppConstant.CANCELLED_ACCOUNT_NAME, ""), uid);
        backupRunnable = new BackupRunnable(client, user, items,
                new BuCallback<Void>() {
                    @Override
                    public void success(Void data) {
                        setBusyStatus(3, false);
                    }

                    @Override
                    public void fail(String msg) {
                        setBusyStatus(3, false);
                    }

                    @Override
                    public void interrupt() {
                        setBusyStatus(3, false);
                    }
                });
        new Thread(backupRunnable).start();
    }

    private void stopBackup() {
        log.info("中断任务中...");
        backupRunnable.setInterrupt(true);
    }

}