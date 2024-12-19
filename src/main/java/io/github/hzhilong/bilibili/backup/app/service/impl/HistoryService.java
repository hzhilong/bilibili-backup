package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.Cursor;
import io.github.hzhilong.bilibili.backup.api.bean.History;
import io.github.hzhilong.bilibili.backup.api.bean.HistoryBusiness;
import io.github.hzhilong.bilibili.backup.api.bean.HistoryItem;
import io.github.hzhilong.bilibili.backup.api.bean.page.HistoryPageData;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BackupRestoreResult;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.service.SegmentableBackupRestoreService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史记录
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class HistoryService extends SegmentableBackupRestoreService<History> {

    public static final String buName = "历史记录";
    public static final String buEnName = "History";

    private final PageApi<HistoryPageData, History> pageApi;

    public HistoryService(OkHttpClient client, User user, String path) {
        super(client, user, path);
        this.pageApi = new PageApi<>(client, signUser(), "https://api.bilibili.com/x/web-interface/history/cursor",
                queryParams -> {
                    queryParams.put("type", HistoryBusiness.ALL.toString());
                    queryParams.put("ps", "20");
                }, HistoryPageData.class, History.class);
    }

    public List<History> getList(BusinessType businessType) throws BusinessException {
        int pn = 1;
        int maxSize = PageApi.MAX_SIZE;
        if (BusinessType.BACKUP.equals(businessType)) {
            pn = getSegmentBackupPageNo();
            maxSize = getSegmentBackupMaxSize();
        }
        return pageApi.getAllData((pageData, queryParams) -> {
            if (pageData != null) {
                Cursor cursor = pageData.getCursor();
                queryParams.put("max", String.valueOf(cursor.getMax()));
                queryParams.put("view_at", String.valueOf(cursor.getViewAt()));
            }
        }, pn, maxSize);
    }

    private void addData(History data) throws BusinessException {
        HistoryItem historyItem = data.getHistory();
        ApiResult<?> apiResult;
        if (HistoryBusiness.ARCHIVE.equals(data.getHistory().getBusiness())) {
            // 稿件
            apiResult = new ModifyApi<JSONObject>(client, user, "https://api.bilibili.com/x/v2/history/report", JSONObject.class)
                    .modify(new HashMap<String, String>() {{
                        // 稿件avid
                        put("aid", String.valueOf(historyItem.getOid()));
                        // 视频cid，用于识别分P
                        put("cid", String.valueOf(historyItem.getCid()));
                        // 观看进度，单位为秒<br />默认为0
                        put("progress", String.valueOf(data.getProgress()));
                    }});
        } else if (HistoryBusiness.LIVE.equals(data.getHistory().getBusiness())) {
            // 直播
            apiResult = new ModifyApi<JSONObject>(client, user, "https://api.live.bilibili.com/xlive/web-room/v1/index/roomEntryAction", JSONObject.class)
                    .modify(new HashMap<String, String>() {{
                        put("room_id", String.valueOf(historyItem.getOid()));
                        put("platform", "web");
                        put("visit_id", "");
                    }});
        } else if (HistoryBusiness.PGC.equals(data.getHistory().getBusiness())) {
            // 剧集
            apiResult = new ModifyApi<JSONObject>(client, user, "https://api.bilibili.com/x/click-interface/web/heartbeat",
                    JSONObject.class)
                    .modify(new HashMap<String, String>() {{
                        // 稿件avid
                        put("aid", String.valueOf(historyItem.getOid()));
                        // 视频cid，用于识别分P
                        put("cid", String.valueOf(historyItem.getCid()));
                        // 观看进度，单位为秒<br />默认为0
                        put("played_time", String.valueOf(data.getProgress()));
                    }});
        } else {
            throw new BusinessException(String.format("历史记录[%s]：该类型不支持还原", data.getTitle()));
        }
        if (apiResult.isFail()) {
            throw new BusinessException(apiResult);
        }
    }

    @Override
    public List<BackupRestoreResult<List<History>>> backup() throws BusinessException {
        return createResults(backupData(buName, new BackupCallback<List<History>>() {
            @Override
            public List<History> getData() throws BusinessException {
                return getList(BusinessType.BACKUP);
            }

            @Override
            public void finished(List<History> data) {
                callbackBackupSegment(pageApi, data);
            }
        }, getSegmentBackupPageNo() > 1));
    }

    @Override
    public List<BackupRestoreResult<List<History>>> restore() throws BusinessException {
        log.info("还原[历史记录]目前仅支持视频/直播");
        return createResults(restoreList(buName, History.class,
                getSegmentRestorePageNo(), getSegmentRestoreMaxSize(),
                new RestoreCallback<History>() {
                    @Override
                    public List<History> getNewList() throws BusinessException {
                        return getList(BusinessType.RESTORE);
                    }

                    @Override
                    public String compareFlag(History data) {
                        HistoryItem history = data.getHistory();
                        if (history != null) {
                            return String.valueOf(history.getOid());
                        }
                        return String.valueOf(data.getKid());
                    }

                    @Override
                    public String dataName(History data) {
                        HistoryItem history = data.getHistory();
                        if (history == null) {
                            return String.format("[%s]", data.getTitle());
                        } else {
                            HistoryBusiness business = history.getBusiness();
                            if (business == null) {
                                return String.format("[未能识别的历史记录][%s]", data.getTitle());
                            } else {
                                return String.format("[%s历史记录][%s]", business.getDesc(), data.getTitle());
                            }
                        }
                    }

                    @Override
                    public void restoreData(History data) throws BusinessException {
                        addData(data);
                    }

                    @Override
                    public void finished(List<History> oldData) {
                        callbackRestoreSegment(oldData);
                    }
                }));
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put(buName, buEnName);
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", buName);
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        if (pageApi != null) {
            pageApi.setInterrupt(interrupt);
        }
        super.setInterrupt(interrupt);
    }
}
