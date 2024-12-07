package top.ybgnb.bilibili.backup.biliapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.app.business.BusinessType;
import top.ybgnb.bilibili.backup.biliapi.bean.Relation;
import top.ybgnb.bilibili.backup.biliapi.bean.RelationAct;
import top.ybgnb.bilibili.backup.biliapi.bean.page.PageData;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.service.RelationService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BlackService
 * @Description 黑名单
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class BlackService extends RelationService {

    private PageApi<PageData<Relation>, Relation> pageApi;

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

    public List<Relation> getList(BusinessType businessType) throws BusinessException {
        if (BusinessType.BACKUP.equals(businessType)) {
            return pageApi.getAllData(getSegmentBackupPageNo(), getSegmentBackupMaxSize());
        }
        return pageApi.getAllData();
    }

    @Override
    public void backup() throws BusinessException {
        backupData("黑名单", new BackupCallback<List<Relation>>() {
            @Override
            public List<Relation> getData() throws BusinessException {
                return getList(BusinessType.BACKUP);
            }

            @Override
            public void finished(List<Relation> data) throws BusinessException {
                callbackBackupSegment(pageApi, data);
            }
        }, getSegmentBackupPageNo() > 1);
    }

    @Override
    public void restore() throws BusinessException {
        restoreList("黑名单", Relation.class, getSegmentRestorePageNo(),
                getSegmentRestoreMaxSize(), new RestoreCallback<Relation>() {
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
                        return String.format("用户[%s]", data.getUname());
                    }

                    @Override
                    public void restoreData(Relation data) throws BusinessException {
                        modify(data, RelationAct.BLOCK);
                    }

                    @Override
                    public void finished(List<Relation> oldData) throws BusinessException {
                        callbackRestoreSegment(oldData);
                    }
                });
    }

    @Override
    public void initFileName(Map<String, String> fileNames) {
        fileNames.put("黑名单", "Black");
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "黑名单");
    }
}
