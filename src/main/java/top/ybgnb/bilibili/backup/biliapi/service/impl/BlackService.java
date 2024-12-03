package top.ybgnb.bilibili.backup.biliapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.Relation;
import top.ybgnb.bilibili.backup.biliapi.bean.RelationAct;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.PageApi;
import top.ybgnb.bilibili.backup.biliapi.service.RelationService;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.io.File;
import java.util.List;

/**
 * @ClassName BlackService
 * @Description 黑名单
 * @Author hzhilong
 * @Time 2024/9/26
 * @Version 1.0
 */
@Slf4j
public class BlackService extends RelationService {

    public BlackService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }

    public List<Relation> getList() throws BusinessException {
        return new PageApi<>(client, user,
                "https://api.bilibili.com/x/relation/blacks",
                queryParams -> {
                    queryParams.put("re_version", "0");
                    queryParams.put("jsonp", "jsonp");
                },
                Relation.class).getAllData();
    }

    @Override
    public void backup() throws BusinessException {
        backupData("黑名单", () -> getList());
    }

    @Override
    public void restore() throws BusinessException {
        restoreList("黑名单", Relation.class, new RestoreCallback<Relation>() {
            @Override
            public List<Relation> getNewList() throws BusinessException {
                return getList();
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
        });
    }

    @Override
    public int getBackupCount(File dir) throws BusinessException {
        return getBackupListSize(dir, "", "黑名单");
    }
}
