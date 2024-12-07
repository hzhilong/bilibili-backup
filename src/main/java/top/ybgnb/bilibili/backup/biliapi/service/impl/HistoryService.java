package top.ybgnb.bilibili.backup.biliapi.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.business.BusinessType;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.Cursor;
import top.ybgnb.bilibili.backup.biliapi.bean.History;
import top.ybgnb.bilibili.backup.biliapi.bean.HistoryBusiness;
import top.ybgnb.bilibili.backup.biliapi.bean.HistoryItem;
import top.ybgnb.bilibili.backup.biliapi.bean.page.HistoryPageData;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.ModifyApi;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.service.SegmentableBackupRestoreService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName HistoryService
 * @Description 历史记录
 * @Author hzhilong
 * @Time 2024/12/06
 * @Version 1.0
 */
@Slf4j
public class HistoryService extends SegmentableBackupRestoreService {

    public static final String buName = "历史记录";
    public static final String buEnName = "History";

    private PageApi<HistoryPageData, History> pageApi;

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
        if (apiResult._isFail()) {
            throw new BusinessException(apiResult);
        }
    }

    @Override
    public void backup() throws BusinessException {
        backupData(buName, new BackupCallback<List<History>>() {
            @Override
            public List<History> getData() throws BusinessException {
                return getList(BusinessType.BACKUP);
            }

            @Override
            public void finished(List<History> data) throws BusinessException {
                callbackBackupSegment(pageApi, data);
            }
        }, getSegmentBackupPageNo() > 1);
    }

    @Override
    public void restore() throws BusinessException {
        log.info("还原[历史记录]目前仅支持视频/直播");
        restoreList(buName, History.class, getSegmentRestorePageNo(),
                getSegmentRestoreMaxSize(), new RestoreCallback<History>() {
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
                    public void finished(List<History> oldData) throws BusinessException {
                        callbackRestoreSegment(oldData);
                    }
                });

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
