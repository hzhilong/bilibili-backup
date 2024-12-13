package io.github.hzhilong.bilibili.backup.gui.segment;

import lombok.extern.slf4j.Slf4j;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreService;
import io.github.hzhilong.bilibili.backup.app.service.SegmentableBackupRestoreService;
import io.github.hzhilong.bilibili.backup.app.state.appdata.AppData;
import io.github.hzhilong.bilibili.backup.gui.worker.BackupRestoreRunnable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 分段处理工具
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class SegmentUtil {

    public static AppData appData = AppData.getInstance();

    public static List<JRadioButton> createSegmentButtons(JPanel panel) {
        List<JRadioButton> buttons = new ArrayList<>(SegmentMaxSize.values().length);
        ButtonGroup buttonGroup = new ButtonGroup();
        for (SegmentMaxSize maxSize : SegmentMaxSize.values()) {
            JRadioButton jRadioButton = new JRadioButton(maxSize.toString());
            if (buttons.isEmpty()) {
                jRadioButton.setSelected(true);
            }
            buttons.add(jRadioButton);
            buttonGroup.add(jRadioButton);
            panel.add(jRadioButton);
        }
        return buttons;
    }

    public static SegmentMaxSize getSegmentMaxSize(List<JRadioButton> segmentButtons) {
        for (JRadioButton segmentButton : segmentButtons) {
            if (segmentButton.isSelected()) {
                return SegmentMaxSize.parse(segmentButton.getText());
            }
        }
        return SegmentMaxSize.SIZE_ALL;
    }

    public static void handle(Component windows, BusinessType businessType, BackupRestoreRunnable runnable, List<JRadioButton> segmentButtons) {
        SegmentMaxSize segmentMaxSize = getSegmentMaxSize(segmentButtons);
        if (SegmentMaxSize.SIZE_ALL.equals(segmentMaxSize)) {
            log.debug("[{}] 未选择分段处理", businessType);
            return;
        }
        int maxSize = segmentMaxSize.getMaxSize();
        log.info("[{}] 已勾选分段处理：{}", businessType, maxSize);
        SavedUser user = runnable.getUser();
        String backupDirPath = runnable.getBackupDirPath();
        String uid = String.valueOf(user.getMid());
        LinkedHashMap<BackupRestoreItem, BackupRestoreService> itemServices = runnable.getBackupRestoreItemServices();
        List<BackupRestoreItem> items = new ArrayList<>(itemServices.size());
        LinkedHashMap<BackupRestoreItem, SegmentableBackupRestoreService> oldSegmentServices = new LinkedHashMap<>();
        LinkedHashMap<BackupRestoreItem, SegmentConfig> oldSegmentItems = new LinkedHashMap<>();
        List<BackupRestoreItem> newSegmentItems = new ArrayList<>();
        for (Map.Entry<BackupRestoreItem, BackupRestoreService> entry : itemServices.entrySet()) {
            BackupRestoreItem item = entry.getKey();
            BackupRestoreService service = entry.getValue();
            log.info("正在判断[{}]分段处理：{}，maxSize: {}", businessType, item.getName(), maxSize);
            if (service instanceof SegmentableBackupRestoreService) {
                SegmentableBackupRestoreService segmentService = (SegmentableBackupRestoreService) service;
                log.info("可分段处理：{}", item.getName());
                SegmentConfig segmentConfig = appData.getSegmentConfig(uid, businessType, item);
                if (segmentConfig != null) {
                    log.info("存在未完成的分段处理：{}", item.getName());
                    oldSegmentItems.put(item, segmentConfig);
                    oldSegmentServices.put(item, segmentService);
                } else {
                    log.info("设置新的分段处理：{}", item.getName());
                    newSegmentItems.add(item);
                    segmentConfig = new SegmentConfig(businessType, uid, item, backupDirPath, 1);
                }
                SegmentConfig finalSegmentConfig = segmentConfig;
                if (BusinessType.BACKUP.equals(businessType)) {
                    segmentService.setSegmentBackupMaxSize(maxSize);
                } else if (BusinessType.RESTORE.equals(businessType)) {
                    segmentService.setSegmentRestoreMaxSize(maxSize);
                }
                segmentService.setSegmentCallBack(new SegmentableBackupRestoreService.SegmentCallback() {
                    @Override
                    public void unfinished(int currPage, int currPageSize, int currSize) {
                        finalSegmentConfig.setPath(segmentService.getPath());
                        finalSegmentConfig.setBusinessType(businessType);
                        finalSegmentConfig.setNextPage(currPage + 1);
                        appData.setSegmentConfig(finalSegmentConfig);
                        log.info("当前[{} {}]分段处理完成，页码：{}", businessType, item.getName(), currPage);
                    }

                    @Override
                    public void finished(int currPage, int currPageSize, int currSize) {
                        appData.delSegmentConfig(finalSegmentConfig);
                        log.info("[{} {}]分段处理已全部完成", businessType, item.getName());
                    }
                });
            } else {
                log.info("不支持分段处理：{}", item.getName());
            }
            items.add(item);
        }

        if (!oldSegmentItems.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("用户").append(user.getMid()).append("存在未完成的分段").append(businessType.getName()).append("，是否继续？\n");
            int i = 1;
            for (Map.Entry<BackupRestoreItem, SegmentConfig> entry : oldSegmentItems.entrySet()) {
                sb.append("\t").append(i).append(".").append(entry.getKey().getName()).append(" 下一页 ").append(entry.getValue().getNextPage()).append("\n");
            }
            sb.append("\n【是】仅继续").append(businessType).append("以上项目");
            sb.append("\n【否】终止上一次的分段").append(businessType);
            int result = JOptionPane.showConfirmDialog(windows, sb.toString(), "提示",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                for (Map.Entry<BackupRestoreItem, SegmentableBackupRestoreService> entry : oldSegmentServices.entrySet()) {
                    // 设置为上一次处理的进度
                    SegmentableBackupRestoreService service = entry.getValue();
                    SegmentConfig config = oldSegmentItems.get(entry.getKey());
                    if (BusinessType.BACKUP.equals(businessType)) {
                        service.setSegmentBackupPageNo(config.getNextPage());
                    } else if (BusinessType.RESTORE.equals(businessType)) {
                        service.setSegmentRestorePageNo(config.getNextPage());
                    }
                    service.setPath(config.getPath());
                }
                // 重置线程执行的服务
                itemServices.clear();
                itemServices.putAll(oldSegmentServices);
            }
        }
    }


}
