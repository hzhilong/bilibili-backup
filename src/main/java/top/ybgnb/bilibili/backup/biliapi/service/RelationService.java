package top.ybgnb.bilibili.backup.biliapi.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import top.ybgnb.bilibili.backup.biliapi.bean.ApiResult;
import top.ybgnb.bilibili.backup.biliapi.bean.Relation;
import top.ybgnb.bilibili.backup.biliapi.bean.RelationAct;
import top.ybgnb.bilibili.backup.biliapi.error.BusinessException;
import top.ybgnb.bilibili.backup.biliapi.request.AddQueryParams;
import top.ybgnb.bilibili.backup.biliapi.request.BaseApi;
import top.ybgnb.bilibili.backup.biliapi.request.ModifyApi;
import top.ybgnb.bilibili.backup.biliapi.user.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RelationService
 * @Description 关系
 * @Author hzhilong
 * @Time 2024/9/30
 * @Version 1.0
 */
@Slf4j
public abstract class RelationService extends BackupRestoreService {

    private int modifyCount;

    public RelationService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }


    /**
     * 防止风控
     *
     * @param relation
     * @throws BusinessException
     */
    protected void preventRiskCtrl(Relation relation) throws BusinessException {
        log.info(String.format("搜索UP主[%s](防止风控)", relation.getUname()));
        new BaseApi<JSONObject>(client, user,
                "https://api.bilibili.com/x/web-interface/wbi/search/all/v2", new AddQueryParams() {
            @Override
            public void addQueryParams(Map<String, String> queryParams) {
                queryParams.put("__refresh__", "true");
                queryParams.put("_extra", "");
                queryParams.put("context", "");
                queryParams.put("page", "1");
                queryParams.put("page_size", "42");
                queryParams.put("order", "");
                queryParams.put("pubtime_begin_s", "0");
                queryParams.put("pubtime_end_s", "0");
                queryParams.put("duration", "");
                queryParams.put("from_source", "");
                queryParams.put("from_spmid", "333.337");
                queryParams.put("platform", "pc");
                queryParams.put("highlight", "1");
                queryParams.put("single_column", "0");
                queryParams.put("keyword", relation.getUname());
                queryParams.put("ad_resource", "5646");
                queryParams.put("source_tag", "3");
            }
        }, true, JSONObject.class).apiGet();
        try {
            Thread.sleep(1111);
        } catch (InterruptedException e) {

        }
//        log.info(String.format("访问UP主[%s]个人空间(防止风控)", relation.getUname()));
//        new BaseApi<String>(client, user, "https://space.bilibili.com/" + relation.getMid() + "?spm_id_from=333.337.0.0",
//                true, String.class).htmlGet();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//
//        }
        log.info(String.format("获取UP主[%s]关注状态(防止风控)", relation.getUname()));
        new BaseApi<JSONObject>(client, user, "https://api.bilibili.com/x/relation/stat", new AddQueryParams() {
            @Override
            public void addQueryParams(Map<String, String> queryParams) {
                queryParams.put("vmid", String.valueOf(relation.getMid()));
            }
        }, true, JSONObject.class).apiGet();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
    }

    /**
     * 操作用户关系
     *
     * @param relation
     * @param act
     * @throws BusinessException
     */
    public void modify(Relation relation, RelationAct act) throws BusinessException {
        preventRiskCtrl(relation);

        ApiResult<Void> apiResult = new ModifyApi<Void>(client, user, "https://api.bilibili.com/x/relation/modify",
                Void.class).modify(
                new HashMap<String, String>() {{
                    put("fid", String.valueOf(relation.getMid()));
                    put("act", String.valueOf(act.getCode()));
                    put("re_src", "120");
                    put("spmid", "333.337.0.0");
                    put("extend_content", "{\"entity\":\"query\",\"entity_id\":\"" + relation.getUname() + "\"}");
                }});
        if (apiResult._isSuccess()) {
            log.info(String.format("成功%s用户：%s", act.getName(), relation.getUname()));
        } else {
            log.info(String.format("无法%s用户：[%s](%s)", act.getName(), relation.getUname(), apiResult.getMessage()));
            if (22001 == apiResult.getCode()) {
                log.info("不能对自己进行此操作");
            } else if (22002 == apiResult.getCode()) {
                log.info("因对方隐私设置，你还不能关注");
            } else if (22003 == apiResult.getCode()) {
                log.info("关注失败，请将该用户移除黑名单之后再试");
            } else if (22013 == apiResult.getCode()) {
                log.info("账号已注销，无法完成操作");
            } else if (22014 == apiResult.getCode()) {
                log.info("已经关注用户，无法重复关注");
            } else if (22120 == apiResult.getCode()) {
                log.info("重复加入黑名单");
            } else if (40061 == apiResult.getCode()) {
                log.info("用户不存在");
            } else {
                throw new BusinessException("操作失败，可能被风控，建议晚点再试", true);
            }
        }
        modifyCount++;
        if (modifyCount % 100 == 0) {
            log.info(String.format("已操作[%s]100次，请休息10分钟（我也不知道要等多久，反正短时间内频繁操作容易风控 －_－b）", act.getName()));
            try {
                Thread.sleep(10 * 60 * 1000);
            } catch (InterruptedException e) {
            }
        }
    }

}
