package io.github.hzhilong.bilibili.backup.app.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.error.NeedEndLoopException;
import io.github.hzhilong.base.utils.FileUtil;
import io.github.hzhilong.base.utils.ListUtil;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BackupRestoreItemInfo;
import io.github.hzhilong.bilibili.backup.app.bean.BusinessResult;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 备份还原服务基类
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public abstract class BackupRestoreService<T> extends BaseService implements BackupRestoreItemInfo {

    @Setter
    @Getter
    public String path;

    private final HashMap<String, String> mapFileName;

    /**
     * 还原时忽略新账号现有的数据，直接还原
     */
    @Setter
    @Getter
    private boolean directRestore;

    /**
     * 还原失败时，继续还原下一个数据
     */
    @Setter
    @Getter
    private boolean allowFailure;

    public BackupRestoreService(OkHttpClient client, User user, String path) {
        super(client, user);
        this.path = path;
        this.mapFileName = new HashMap<>(2);
        this.directRestore = false;
        this.allowFailure = false;
        this.initFileName(mapFileName);
    }

    public abstract List<BusinessResult<List<T>>> backup() throws BusinessException;

    public abstract List<BusinessResult<List<T>>> restore() throws BusinessException;

    @SafeVarargs
    protected final List<BusinessResult<List<T>>> createResults(BusinessResult<List<T>>... results) {
        if (results == null) {
            return new ArrayList<>(0);
        } else {
            List<BusinessResult<List<T>>> businessResults = new ArrayList<>(results.length);
            Collections.addAll(businessResults, results);
            return businessResults;
        }
    }

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

    public interface BackupCallback<DB> {
        DB getData() throws BusinessException;

        default DB processData(DB data) throws BusinessException {
            return data;
        }

        default void finished(DB data) {
        }
    }

    protected <D> BusinessResult<D> backupData(String buName, BackupCallback<D> callback) throws BusinessException {
        return backupData(buName, callback, false);
    }

    protected <D> BusinessResult<D> backupData(String buName, BackupCallback<D> callback, boolean isAppendData) throws BusinessException {
        return backupData("", buName, callback, isAppendData);
    }

    protected <D> BusinessResult<D> backupData(String appendDir, String buName, BackupCallback<D> callback) throws BusinessException {
        return backupData(appendDir, buName, callback, false);
    }

    protected <D> BusinessResult<D> backupData(String appendDir, String buName, BackupCallback<D> callback, boolean isAppendData) throws BusinessException {
        return backupData(appendDir, buName, callback, isAppendData, true);
    }

    protected <D> BusinessResult<D> backupData(String appendDir, String buName, BackupCallback<D> callback, boolean isAppendData, boolean printLog) throws BusinessException {
        BusinessResult<D> result = new BusinessResult<>();
        result.setBusinessType(BusinessType.BACKUP);
        result.setItemName(buName);
        handleInterrupt();
        if (printLog) {
            log.info("正在备份[{}]...", buName);
        }
        D data = null;
        try {
            data = callback.getData();
        } catch (BusinessException e) {
            result.setFailed("获取数据失败：" + e.getMessage());
            return result;
        }
        result.setData(data);
        try {
            data = callback.processData(data);
            result.setData(data);
        } catch (BusinessException e) {
            result.setFailed("处理数据失败：" + e.getMessage());
            return result;
        }
        try {
            writeJsonFile(path, appendDir, buName, data, isAppendData);
        } catch (BusinessException e) {
            result.setFailed("写入文件失败：" + e.getMessage());
            return result;
        }
        if (printLog) {
            String msg;
            if (data instanceof List) {
                List list = (List) data;
                msg = String.format("成功备份%s条[%s]数据", list.size(), buName);
            } else {
                msg = String.format("成功备份%s数据", buName);
            }
            log.info(msg);
            result.setSuccess(msg);
        }
        callback.finished(data);
        return result;
    }

    public interface RestoreCallback<DR> {
        List<DR> getNewList() throws BusinessException;

        /**
         * 比较的标志
         */
        String compareFlag(DR data);

        String dataName(DR data);

        void restoreData(DR data) throws BusinessException;

        default void finished(List<DR> oldData) {
        }
    }

    protected <R> BusinessResult<List<R>> restoreList(String buName, Class<R> dataClass, RestoreCallback<R> callback) throws BusinessException {
        return restoreList("", buName, dataClass, callback);
    }

    protected <R> BusinessResult<List<R>> restoreList(String buName, Class<R> dataClass, int page, int pageSize, RestoreCallback<R> callback) throws BusinessException {
        return restoreList("", buName, dataClass, page, pageSize, callback);
    }

    protected <R> BusinessResult<List<R>> restoreList(String appendDir, String buName, Class<R> dataClass, RestoreCallback<R> callback) throws BusinessException {
        return restoreList(appendDir, buName, dataClass, 1, -1, callback);
    }

    protected <R> BusinessResult<List<R>> restoreList(String appendDir, String buName, Class<R> dataClass, int page, int pageSize, RestoreCallback<R> callback) throws BusinessException {
        BusinessResult<List<R>> result = new BusinessResult<>();
        result.setBusinessType(BusinessType.RESTORE);
        result.setItemName(buName);
        handleInterrupt();
        log.info("正在还原[{}]...", buName);
        List<R> oldList = null;
        try {
            oldList = getBackupList(path, appendDir, buName, dataClass);
        } catch (BusinessException e) {
            result.setFailed("解析旧账号数据失败：" + e.getMessage());
            return result;
        }
        log.info("解析旧账号{}：{}条数据", buName, ListUtil.getSize(oldList));
        if (ListUtil.isEmpty(oldList)) {
            log.info("{}为空，无需还原", buName);
            result.setFailed("数据为空，无需还原");
            return result;
        }
        Set<String> newListIds = new HashSet<>();
        if (directRestore) {
            log.info("还原时忽略新账号现有的数据，直接还原...");
        } else {
            log.info("获取新账号{}...", buName);
            List<R> newList = null;
            try {
                newList = callback.getNewList();
            } catch (BusinessException e) {
                result.setFailed("获取新账号数据失败：" + e.getMessage());
                return result;
            }
            log.info("获取新账号{}：{}条数据", buName, ListUtil.getSize(newList));
            for (R data : newList) {
                if (data != null) {
                    newListIds.add(callback.compareFlag(data));
                }
            }
        }

        log.info("开始遍历旧账号{}...", buName);
        List<R> restoredList = new ArrayList<>();
        // 反序还原
        Collections.reverse(oldList);
        // 截取旧数据
        if (pageSize > 0 && page > 0) {
            log.info("正在还原第{}页，分页大小：{}", page, pageSize);
            int start = (page - 1) * pageSize;
            oldList = oldList.subList(start, Math.min(start + pageSize, oldList.size()));
        }
        String logNoFormat = StringUtils.getLogNoFormat(oldList.size());
        for (int i = 0; i < oldList.size(); i++) {
            R oldData = oldList.get(i);
            handleInterrupt();
            if (oldData == null || "null".equals(callback.compareFlag(oldData))) {
                log.info("{}失效的{}，跳过还原", String.format(logNoFormat, i + 1), buName);
                continue;
            }
            String dataName = callback.dataName(oldData);
            if (newListIds.contains(callback.compareFlag(oldData))) {
                log.info("{}{}已在新账号{}中", String.format(logNoFormat, i + 1), dataName, buName);
                restoredList.add(oldData);
            } else {
                try {
                    callback.restoreData(oldData);
                    log.info("{}{}还原成功", String.format(logNoFormat, i + 1), dataName);
                    restoredList.add(oldData);
                    sleep(restoredList.size());
                } catch (BusinessException e) {
                    log.info("{}{}还原失败：{}", String.format(logNoFormat, i + 1), dataName, e.getMessage());
                    if (!allowFailure && (e instanceof NeedEndLoopException)) {
                        // 不允许失败继续，且内部项目遇到需跳出循环的异常
                        result.setFailed("还原失败" + e.getMessage());
                        break;
                    }else{
                        sleep(restoredList.size());
                    }
                }
            }
        }
        callback.finished(oldList);
        result.setData(restoredList);
        result.setSuccess(String.format("成功还原%s条[%s]数据", restoredList.size(), buName));
        return result;
    }

    private <D> List<D> getBackupList(String dirPath, String appendDir, String buName, Class<D> dataClass) throws BusinessException {
        return JSONObject.parseObject(readJsonFile(dirPath, appendDir, buName),
                new TypeReference<List<D>>(dataClass) {
                });
    }

    protected int getBackupListSize(File dir, String appendDir, String buName) throws BusinessException {
        return ListUtil.getSize(getBackupList(dir.getPath() + File.separator, appendDir, buName, JSONObject.class));
    }

    /**
     * 初始化文件名映射（兼容旧版本备份文件）
     *
     * @param fileNames
     */
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

    public abstract List<BusinessResult<List<T>>> clear() throws BusinessException;

    public interface ClearListCallback<DR> {

        List<DR> getList() throws BusinessException;

        void delData(DR data) throws BusinessException;

        String dataName(DR data);
    }

    protected <R> BusinessResult<List<R>> clearList(String buName, ClearListCallback<R> callback) throws BusinessException {
        BusinessResult<List<R>> result = new BusinessResult<>();
        result.setBusinessType(BusinessType.CLEAR);
        result.setItemName(buName);
        handleInterrupt();
        log.info("正在清空[{}]...", buName);
        log.info("获取账号{}...", buName);
        List<R> list = null;
        try {
            list = callback.getList();
        } catch (BusinessException e) {
            result.setFailed("获取新账号数据失败：" + e.getMessage());
            return result;
        }
        int size = ListUtil.getSize(list);
        log.info("获取账号{}：{}条数据", buName, size);
        List<R> deletedList = new ArrayList<>();
        // 连续失败的次数
        int continuousFailCount = 0;
        String logNoFormat = StringUtils.getLogNoFormat(size);
        for (int i = 0; i < size; i++) {
            handleInterrupt();
            R data = list.get(i);
            if (data != null) {
                String dataName = callback.dataName(data);
                log.info("{}开始删除{}", String.format(logNoFormat, i + 1), dataName);
                try {
                    callback.delData(data);
                    deletedList.add(data);
                    if (i <= size - 1) {
                        sleep(deletedList.size());
                        continuousFailCount = 0;
                    }
                } catch (BusinessException e) {
                    log.info("{}删除{}失败：{}", String.format(logNoFormat, i + 1), dataName, e.getMessage());
                    continuousFailCount++;
                    if (continuousFailCount >= 5) {
                        throw new BusinessException("连续失败的次数过多，已暂停处理");
                    }
                }
            }
        }
        if (deletedList.isEmpty()) {
            if (size == 0) {
                result.setSuccess("数据为空，无需清空");
            } else {
                result.setFailed("清空失败，成功删除的个数为0");
            }
        } else {
            result.setData(deletedList);
            result.setSuccess(String.format("成功清空%s条[%s]数据", deletedList.size(), buName));
        }
        return result;
    }

    public interface ClearDataCallback {
        void delData() throws BusinessException;
    }

    protected <R> BusinessResult<List<R>> clearData(String buName, ClearDataCallback callback) throws BusinessException {
        BusinessResult<List<R>> result = new BusinessResult<>();
        result.setBusinessType(BusinessType.CLEAR);
        result.setItemName(buName);
        handleInterrupt();
        log.info("正在清空[{}]...", buName);
        try {
            callback.delData();
            log.info("成功清空[{}]...", buName);
            result.setSuccess("清空成功");
        } catch (BusinessException e) {
            result.setFailed("清空失败：" + e.getMessage());
        }
        return result;
    }
}
