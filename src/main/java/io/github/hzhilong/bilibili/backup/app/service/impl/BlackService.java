package io.github.hzhilong.bilibili.backup.app.service.impl;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.Relation;
import io.github.hzhilong.bilibili.backup.api.bean.RelationAct;
import io.github.hzhilong.bilibili.backup.api.bean.page.PageData;
import io.github.hzhilong.bilibili.backup.api.request.PageApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.bean.BusinessResult;
import io.github.hzhilong.bilibili.backup.app.business.BusinessType;
import io.github.hzhilong.bilibili.backup.app.business.IBusinessType;
import io.github.hzhilong.bilibili.backup.app.service.RelationService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 黑名单
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class BlackService extends RelationService {

    private final PageApi<PageData<Relation>, Relation> pageApi;

    public BlackService(OkHttpClient client, User user, String path) {
        super(client, user, path);
        this.pageApi = new PageApi<>(client, user,
                "https://api.bilibili.com/x/relation/blacks",
                queryParams -> {
                    queryParams.put("re_version", "0");
                    queryParams.put("jsonp", "jsonp");
                },
                Relation.class);
    }

    public List<Relation> getList(IBusinessType businessType) throws BusinessException {
        if (BusinessType.BACKUP.equals(businessType)) {
            return pageApi.getAllData(getSegmentBackupPageNo(), getSegmentBackupMaxSize());
        }
        return pageApi.getAllData();
    }

    @Override
    public List<BusinessResult<List<Relation>>> backup() throws BusinessException {
        return createResults(backupData("黑名单",
                new BackupCallback<List<Relation>>() {
                    @Override
                    public List<Relation> getData() throws BusinessException {
                        return getList(BusinessType.BACKUP);
                    }

                    @Override
                    public void finished(List<Relation> data) {
                        callbackBackupSegment(pageApi, data);
                    }
                }, getSegmentBackupPageNo() > 1));
    }

    @Override
    public List<BusinessResult<List<Relation>>> restore() throws BusinessException {
        return createResults(
                restoreList("黑名单", Relation.class,
                        getSegmentRestorePageNo(), getSegmentRestoreMaxSize(),
                        new RestoreCallback<Relation>() {
                            @Override
                            public List<Relation> getNewList() throws BusinessException {
                                return getList(BusinessType.RESTORE);
                            }

                            @Override
                            public String compareFlag(Relation data) {
                                return String.valueOf(data.getMid());
                            }

                            @Override
                            public String dataName(Relation data) {
                                return String.format("黑名单[%s]", data.getUname());
                            }

                            @Override
                            public void restoreData(Relation data) throws BusinessException {
                                modify(data, RelationAct.BLOCK);
                            }

                            @Override
                            public void finished(List<Relation> oldData) {
                                callbackRestoreSegment(oldData);
                            }
                        }));
    }

    @Override
    public List<BusinessResult<List<Relation>>> clear() throws BusinessException {
        return createResults(clearList("黑名单",
                new ClearListCallback<Relation>() {
                    @Override
                    public List<Relation> getList() throws BusinessException {
                        return BlackService.this.getList(BusinessType.CLEAR);
                    }

                    @Override
                    public void delData(Relation data) throws BusinessException {
                        BlackService.this.modify(data, RelationAct.UNBLOCK, false);
                    }

                    @Override
                    public String dataName(Relation data) {
                        return String.format("黑名单[%s]", data.getUname());
                    }
                }));
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("黑名单", "Black");
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "黑名单");
    }

    @Override
    public void setInterrupt(boolean interrupt) {
        if (pageApi != null) {
            pageApi.setInterrupt(interrupt);
        }
        super.setInterrupt(interrupt);
    }
}
