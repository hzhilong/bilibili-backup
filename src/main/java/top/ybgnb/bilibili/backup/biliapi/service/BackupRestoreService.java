package top.ybgnb.bilibili.backup.biliapi.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.user.User;
import top.ybgnb.bilibili.backup.biliapi.utils.FileUtil;
import top.ybgnb.bilibili.backup.biliapi.utils.ListUtil;
import top.ybgnb.bilibili.backup.ui.utils.BackupFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName BackupRestoreService
 * @Description 备份还原
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public abstract class BackupRestoreService extends BaseService implements BackupRestoreItemInfo {

    protected final String path;

    public BackupRestoreService(OkHttpClient client, User user, String path) {
        super(client, user);
        this.path = path;
    }

    public abstract void backup() throws BusinessException;

    public abstract void restore() throws BusinessException;

    public void writeJsonFile(String path, String appendDir, String name, Object obj) throws BusinessException {
        try {
            FileUtil.writeJsonFile(path + BackupFileUtil.getPathEnName(appendDir), BackupFileUtil.getEnName(name) + ".json", obj);
        } catch (BusinessException e) {
            // 兼容旧版本数据
            FileUtil.writeJsonFile(path, name + ".json", obj);
        }
    }

    public String readJsonFile(String path, String appendDir, String name) throws BusinessException {
        String appendPathEnName = BackupFileUtil.getPathEnName(appendDir);
        try {
            // 兼容旧版本数据 中 + 中
            return FileUtil.readJsonFile(path + appendDir + "/", name + ".json");
        } catch (BusinessException e1) {
            try {
                return FileUtil.readJsonFile(path + appendPathEnName, BackupFileUtil.getEnName(name) + ".json");
            } catch (BusinessException ex) {
                throw new BusinessException(String.format("[%s]备份文件为空", name));
            }
        }
    }

    public interface BackupCallback<D> {
        D getData() throws BusinessException;

        default D processData(D data) throws BusinessException {
            return data;
        }
    }

    protected <D> D backupData(String buName, BackupCallback<D> callback) throws BusinessException {
        return backupData("", buName, callback);
    }

    protected <D> D backupData(String appendDir, String buName, BackupCallback<D> callback) throws BusinessException {
        if (isInterrupt()) {
            return null;
        }
        log.info("正在备份[{}]...", buName);
        D data = callback.getData();
        callback.processData(data);
        writeJsonFile(path, appendDir, buName, data);
        if (List.class.isAssignableFrom(data.getClass())) {
            List list = (List) data;
            log.info("成功备份{}条[{}]数据", list.size(), buName);
        } else {
            log.info("成功备份[{}]", buName);
        }
        return data;
    }

    public interface RestoreCallback<D> {
        List<D> getNewList() throws BusinessException;

        /**
         * 比较的标志
         *
         * @return
         */
        String compareFlag(D data);

        String dataName(D data);

        void restoreData(D data) throws BusinessException;
    }

    protected <D> List<D> restoreList(String buName, Class<D> dataClass, RestoreCallback<D> callback) throws BusinessException {
        return restoreList("", buName, dataClass, callback);
    }

    protected <D> List<D> restoreList(String appendDir, String buName, Class<D> dataClass, RestoreCallback<D> callback) throws BusinessException {
        if (isInterrupt()) {
            return null;
        }
        log.info("正在还原[{}]...", buName);
        List<D> oldList = getBackupList(path, appendDir, buName, dataClass);
        log.info("解析旧账号{}：{}条数据", buName, ListUtil.getSize(oldList));
        if (ListUtil.isEmpty(oldList)) {
            log.info("{}为空，无需还原", buName);
            return oldList;
        }
        List<D> newList = callback.getNewList();
        log.info("获取新账号{}：{}条数据", buName, ListUtil.getSize(newList));
        Set<String> newListIds = new HashSet<>();
        for (D data : newList) {
            if (data != null) {
                newListIds.add(callback.compareFlag(data));
            }
        }
        log.info("开始遍历旧账号{}...", buName);
        List<D> restoredList = new ArrayList<>();
        // 反序还原
        Collections.reverse(oldList);
        for (D oldData : oldList) {
            if (oldData == null || "null".equals(callback.compareFlag(oldData))) {
                log.info("失效的{}，跳过还原", buName);
                continue;
            }
            if (newListIds.contains(callback.compareFlag(oldData))) {
                log.info("{}已在新账号{}中", callback.dataName(oldData), buName);
            } else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                try {
                    callback.restoreData(oldData);
                    log.info("{}还原成功", callback.dataName(oldData));
                    restoredList.add(oldData);
                } catch (BusinessException e) {
                    log.info("{}还原失败：{}", callback.dataName(oldData), e.getMessage());
                    if (e.isEndLoop()) {
                        break;
                    }
                }
            }
        }
        return restoredList;
    }

    private <D> List<D> getBackupList(String dirPath, String appendDir, String buName, Class<D> dataClass) throws BusinessException {
        return JSONObject.parseObject(readJsonFile(dirPath, appendDir, buName),
                new TypeReference<List<D>>(dataClass) {
                });
    }

    protected int getBackupListSize(File dir, String appendDir, String buName) throws BusinessException {
        return ListUtil.getSize(getBackupList(dir.getPath() + "/", appendDir, buName, JSONObject.class));
    }

}
