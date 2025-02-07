package io.github.hzhilong.bilibili.backup.gui.worker;

import io.github.hzhilong.base.bean.BuCallback;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.baseapp.business.IBusinessType;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BusinessResult;
import io.github.hzhilong.bilibili.backup.app.bean.NeedContext;
import io.github.hzhilong.bilibili.backup.app.bean.SavedUser;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreItem;
import io.github.hzhilong.bilibili.backup.app.service.BackupRestoreService;
import io.github.hzhilong.bilibili.backup.app.service.impl.FavoritesService;
import io.github.hzhilong.bilibili.backup.app.state.setting.AppSettingItems;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 备份还原的线程基类
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public abstract class BackupRestoreRunnable extends BaseRunnable {

    @Getter
    protected SavedUser user;
    protected BuCallback<Void> buCallback;
    protected BackupRestoreService<?> currBackupRestoreService;
    @Getter
    protected LinkedHashMap<BackupRestoreItem, BackupRestoreService> backupRestoreItemServices;
    @Getter
    protected String backupDirPath;
    protected User apiUser;

    public BackupRestoreRunnable(Window parent, String appIconPath, OkHttpClient client, SavedUser user, LinkedHashSet<BackupRestoreItem> backupRestoreItems, String backupDirPath, BuCallback<Void> buCallback) {
        super(client);
        this.client = client;
        this.user = user;
        this.buCallback = buCallback;
        this.backupRestoreItemServices = new LinkedHashMap<>(backupRestoreItems.size());
        if (StringUtils.notEmpty(backupDirPath)) {
            this.backupDirPath = backupDirPath;
        } else {
            this.backupDirPath = String.format("%s%s_%s_%s/", AppConstant.BACKUP_PATH_PREFIX, user.getName(), user.getMid(),
                    new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        }
        this.apiUser = new User(user.getCookie());
        for (BackupRestoreItem item : backupRestoreItems) {
            BackupRestoreService<?> service = item.getServiceBuilder().build(this.client, this.apiUser, this.backupDirPath);
            if (service instanceof NeedContext) {
                NeedContext needContext = (NeedContext) service;
                needContext.setWindow(parent);
                needContext.setAppIconPath(appIconPath);
            }
            service.setDirectRestore(AppSettingItems.DIRECT_RESTORE.getValue());
            service.setAllowFailure(AppSettingItems.ALLOW_FAILURE.getValue());
            if (service instanceof FavoritesService) {
                FavoritesService favoritesService = (FavoritesService) service;
                favoritesService.setSaveToDefaultOnFailure(AppSettingItems.FAV_SAVE_TO_DEFAULT_ON_FAILURE.getValue());
            }
            backupRestoreItemServices.put(item, service);
        }
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
        if (currBackupRestoreService != null) {
            currBackupRestoreService.setInterrupt(interrupt);
        }
    }

    protected abstract List<BusinessResult> runService(BackupRestoreItem item, BackupRestoreService<?> service) throws BusinessException;

    protected abstract IBusinessType getBusinessType();

    private List<BusinessResult> buildResult(BackupRestoreItem item, String failMsg) {
        BusinessResult result = new BusinessResult<>();
        result.setBusinessType(getBusinessType());
        result.setItemName(item.getName());
        result.setFailed(failMsg);
        return Collections.singletonList(result);
    }

    @Override
    public void run() {
        boolean onceSuccessful = false;
        List<List<BusinessResult>> results = new ArrayList<>(backupRestoreItemServices.size());
        List<String> itemNames = new ArrayList<>(backupRestoreItemServices.size());
        // 执行各个项目
        for (Map.Entry<BackupRestoreItem, BackupRestoreService> next : backupRestoreItemServices.entrySet()) {
            BackupRestoreItem item = next.getKey();
            itemNames.add(item.getName());
            BackupRestoreService service = next.getValue();
            try {
                currBackupRestoreService = service;
                results.add(runService(item, currBackupRestoreService));
                onceSuccessful = true;
            } catch (Exception e) {
                String msg = String.format("操作失败，%s", e.getMessage());
                results.add(buildResult(item, msg));
                if (e instanceof BusinessException) {
                    log.info(msg);
                } else {
                    log.error(msg, e);
                }
            }
            if (interrupt) {
                log.info("已中断任务");
                break;
            }
        }
        log.info("\n");
        log.info("┌──────────────────【{}结果】──────────────────", getBusinessType().getName());
        for (int i = 0; i < results.size(); i++) {
            List<BusinessResult> result = results.get(i);
            for (BusinessResult temp : result) {
                log.info("├── [{}]\t{}", temp.getItemName(), temp.getMsg());
            }
        }
        log.info("└────────────────────────────────────────────");
        if (buCallback != null) {
            if (interrupt) {
                buCallback.interrupt();
            } else {
                if (onceSuccessful) {
                    log.info("操作成功！");
                    buCallback.success(null);
                } else {
                    log.info("操作失败！");
                    buCallback.fail("操作失败！");
                }
            }
        }
    }
}
