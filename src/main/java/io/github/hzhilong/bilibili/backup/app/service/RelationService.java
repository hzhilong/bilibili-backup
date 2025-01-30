package io.github.hzhilong.bilibili.backup.app.service;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.app.error.NeedEndLoopException;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.Relation;
import io.github.hzhilong.bilibili.backup.api.bean.RelationAct;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户关系 服务基类
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public abstract class RelationService extends SegmentableBackupRestoreService<Relation> {

    private int modifyCount;

    public RelationService(OkHttpClient client, User user, String path) {
        super(client, user, path);
    }


    /**
     * 防止风控
     */
    protected void preventRiskCtrl(Relation relation) throws BusinessException {
        log.info("搜索UP主[{}](防止风控)", relation.getUname());
        new BaseApi<JSONObject>(client, user,
                "https://api.bilibili.com/x/web-interface/wbi/search/all/v2",
                new AddQueryParams() {
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
        } catch (InterruptedException ignored) {

        }
//        log.info(String.format("访问UP主[%s]个人空间(防止风控)", relation.getUname()));
//        new BaseApi<String>(client, user, "https://space.bilibili.com/" + relation.getMid() + "?spm_id_from=333.337.0.0",
//                true, String.class).htmlGet();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//
//        }
        log.info("获取UP主[{}]关注状态(防止风控)", relation.getUname());
        new BaseApi<JSONObject>(client, user, "https://api.bilibili.com/x/relation/stat",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("vmid", String.valueOf(relation.getMid()));
                    }
                }, true, JSONObject.class).apiGet();
        sleep(0);
    }

    /**
     * 操作用户关系
     */
    public void modify(Relation relation, RelationAct act) throws BusinessException {
        modify(relation, act, true);
    }

    public void modify(Relation relation, RelationAct act, boolean preventRiskCtrl) throws BusinessException {
        // 防止风控
        if (preventRiskCtrl) {
            preventRiskCtrl(relation);
        }

        ApiResult<Void> apiResult = new ModifyApi<Void>(client, user, "https://api.bilibili.com/x/relation/modify",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        if (!RelationAct.BLOCK.equals(act)) {
                            queryParams.put("x-bili-device-req-json", "{\"platform\":\"web\",\"device\":\"pc\",\"spmid\":\"333.1387\"}");
                        }
                    }
                }, Void.class).modify(
                new HashMap<String, String>() {{
                    put("fid", String.valueOf(relation.getMid()));
                    put("act", String.valueOf(act.getCode()));
                    put("re_src", act.getReSrc());
                    put("spmid", "333.337.0.0");
                    put("extend_content", "{\"entity\":\"query\",\"entity_id\":\"" + relation.getMid() + "\"}");
                    put("gaia_source", "web_main");
                    if (!RelationAct.BLOCK.equals(act)) {
                        put("is_from_frontend_component", "true");
                    }
                }});
        if (apiResult.isSuccess()) {
            log.info("成功{}用户：{}", act.getName(), relation.getUname());
        } else {
            log.info("无法{}用户：[{}]({})", act.getName(), relation.getUname(), apiResult.getMessage());
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
                throw new NeedEndLoopException(act.getName() + "失败，可能被风控，建议晚点再试");
            }
        }
        modifyCount++;
        if (modifyCount % 100 == 0) {
            log.info("已操作[{}]100次，请休息10分钟（我也不知道要等多久，反正短时间内频繁操作容易风控 －_－b）", act.getName());
            try {
                Thread.sleep(10 * 60 * 1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
