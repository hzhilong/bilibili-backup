package io.github.hzhilong.bilibili.backup.app.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.base.utils.FileUtil;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.error.EndLoopBusinessException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 备份还原服务基类
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public abstract class BackupRestoreService extends BaseService implements BackupRestoreItemInfo {

    /**
     * 最长的延迟时间 毫秒
     */
    public final static int MAX_DELAY_TIME = 5 * 1000;

    @Setter
    @Getter
    public String path;

    private final HashMap<String, String> mapFileName;

    private final Random random;

    public BackupRestoreService(OkHttpClient client, User user, String path) {
        super(client, user);
        this.path = path;
        this.mapFileName = new HashMap<>(2);
        this.random = new Random();
        this.initFileName(mapFileName);
    }

    public abstract void backup() throws BusinessException;

    public abstract void restore() throws BusinessException;

    public void writeJsonFile(String path, String appendDir, String name, Object obj, boolean isAppendData) throws BusinessException {
        Object content = obj;
        if (isAppendData && obj instanceof List) {
            try {
                String jsonFile = readJsonFile(path, appendDir, name);
                JSONArray jsonArrayOld = JSONArray.parseArray(jsonFile);
                JSONArray jsonArrayNew = JSONArray.parseArray(JSONObject.toJSONString(obj));
                jsonArrayOld.addAll(jsonArrayNew);
                content = jsonArrayOld;
            } catch (Exception ignored) {
            }
        }
        FileUtil.writeJsonFile(path + getEnFilePathName(appendDir), getEnFileName(name) + ".json", content);
    }

    public String readJsonFile(String path, String appendDir, String name) throws BusinessException {
        try {
            // 兼容旧版本数据 中 + 中
            return FileUtil.readJsonFile(path + appendDir + File.separator, name + ".json");
        } catch (BusinessException e1) {
            try {
                return FileUtil.readJsonFile(path + getEnFilePathName(appendDir),
                        getEnFileName(name) + ".json");
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

        default void finished(D data) throws BusinessException {
        }
    }

    protected <D> D backupData(String buName, BackupCallback<D> callback) throws BusinessException {
        return backupData(buName, callback, false);
    }

    protected <D> D backupData(String buName, BackupCallback<D> callback, boolean isAppendData) throws BusinessException {
        return backupData("", buName, callback, isAppendData);
    }

    protected <D> D backupData(String appendDir, String buName, BackupCallback<D> callback) throws BusinessException {
        return backupData(appendDir, buName, callback, false);
    }

    protected <D> D backupData(String appendDir, String buName, BackupCallback<D> callback, boolean isAppendData) throws BusinessException {
        return backupData(appendDir, buName, callback, isAppendData, true);
    }

    protected <D> D backupData(String appendDir, String buName, BackupCallback<D> callback, boolean isAppendData, boolean printLog) throws BusinessException {
        handleInterrupt();
        if (printLog) {
            log.info("正在备份[{}]...", buName);
        }
        D data = callback.getData();
        callback.processData(data);
        writeJsonFile(path, appendDir, buName, data, isAppendData);
        if (printLog) {
            if (List.class.isAssignableFrom(data.getClass())) {
                List<?> list = (List<?>) data;
                log.info("成功备份{}条[{}]数据", list.size(), buName);
            } else {
                log.info("成功备份[{}]", buName);
            }
        }
        callback.finished(data);
        return data;
    }

    public interface RestoreCallback<D> {
        List<D> getNewList() throws BusinessException;

        /**
         * 比较的标志
         */
        String compareFlag(D data);

        String dataName(D data);

        void restoreData(D data) throws BusinessException;

        default void finished(List<D> oldData) throws BusinessException {
        }
    }

    protected <D> List<D> restoreList(String buName, Class<D> dataClass, RestoreCallback<D> callback) throws BusinessException {
        return restoreList("", buName, dataClass, callback);
    }

    protected <D> List<D> restoreList(String buName, Class<D> dataClass, int page, int pageSize, RestoreCallback<D> callback) throws BusinessException {
        return restoreList("", buName, dataClass, page, pageSize, callback);
    }

    protected <D> List<D> restoreList(String appendDir, String buName, Class<D> dataClass, RestoreCallback<D> callback) throws BusinessException {
        return restoreList(appendDir, buName, dataClass, 1, -1, callback);
    }

    protected <D> List<D> restoreList(String appendDir, String buName, Class<D> dataClass, int page, int pageSize, RestoreCallback<D> callback) throws BusinessException {
        handleInterrupt();
        log.info("正在还原[{}]...", buName);
        List<D> oldList = getBackupList(path, appendDir, buName, dataClass);
        log.info("解析旧账号{}：{}条数据", buName, ListUtil.getSize(oldList));
        if (ListUtil.isEmpty(oldList)) {
            log.info("{}为空，无需还原", buName);
            return oldList;
        }
        log.info("获取新账号{}...", buName);
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
        // 截取旧数据
        if (pageSize > 0 && page > 0) {
            log.info("正在还原第{}页，分页大小：{}", page, pageSize);
            int start = (page - 1) * pageSize;
            oldList = oldList.subList(start, Math.min(start + pageSize, oldList.size()));
        }
        for (D oldData : oldList) {
            handleInterrupt();
            if (oldData == null || "null".equals(callback.compareFlag(oldData))) {
                log.info("失效的{}，跳过还原", buName);
                continue;
            }
            String dataName = callback.dataName(oldData);
            if (newListIds.contains(callback.compareFlag(oldData))) {
                log.info("{}已在新账号{}中", dataName, buName);
            } else {
                try {
                    callback.restoreData(oldData);
                    log.info("{}还原成功", dataName);
                    restoredList.add(oldData);
                    sleep(restoredList.size());
                } catch (BusinessException e) {
                    log.info("{}还原失败：{}", dataName, e.getMessage());
                    if (e instanceof EndLoopBusinessException) {
                        break;
                    }
                }
            }
        }
        callback.finished(oldList);
        return restoredList;
    }

    private <D> List<D> getBackupList(String dirPath, String appendDir, String buName, Class<D> dataClass) throws BusinessException {
        return JSONObject.parseObject(readJsonFile(dirPath, appendDir, buName),
                new TypeReference<List<D>>(dataClass) {
                });
    }

    protected int getBackupListSize(File dir, String appendDir, String buName) throws BusinessException {
        return ListUtil.getSize(getBackupList(dir.getPath() + File.separator, appendDir, buName, JSONObject.class));
    }

    public abstract void initFileName(Map<String, String> fileNames);

    /**
     * 获取英文文件名称
     *
     * @param cnName 中文名称
     * @return 英文文件名称
     */
    protected String getEnFileName(String cnName) {
        return mapFileName.getOrDefault(cnName, cnName);
    }

    /**
     * 获取英文路径名称
     *
     * @param cnPathName 中文路径名
     * @return 英文路径名
     */
    private String getEnFilePathName(String cnPathName) {
        if (StringUtils.isEmpty(cnPathName)) {
            return "";
        }
        String enPathName = getEnFileName(cnPathName);
        if (StringUtils.isEmpty(enPathName)) {
            return cnPathName + File.separator;
        } else {
            return enPathName + File.separator;
        }
    }

    public void sleep(int count) {
        try {
            Thread.sleep((1000 + 1000 * Integer.toString(count).length() + random.nextInt(2000)) % MAX_DELAY_TIME);
        } catch (InterruptedException ignored) {
        }
    }

}
