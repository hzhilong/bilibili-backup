package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.CommentDelParams;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 评论
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class CommentService extends BaseService {

    // 动态里的评论
    static Pattern dynamicCommentPattern = Pattern.compile("^bilibili://comment/detail/(\\d+)/(\\d+)/");
    // 视频里的评论
    static Pattern videoCommentPattern = Pattern.compile("^bilibili://video/(\\d+)");


    public CommentService(OkHttpClient client, User user) {
        super(client, user);
    }

    public CommentDelParams del(String rpid, String native_uri, String title, Set<String> deletedCache) throws BusinessException {
        if ("0".equals(rpid)) {
            // 非评论
            return null;
        }
        String type = null;
        String oid = null;

        Matcher dynamicPatternMatcher = dynamicCommentPattern.matcher(native_uri);
        if (dynamicPatternMatcher.find()) {
            // 动态里的评论
            type = dynamicPatternMatcher.group(1);
            oid = dynamicPatternMatcher.group(2);
        } else {
            Matcher videoPatternMatcher = videoCommentPattern.matcher(native_uri);
            if (videoPatternMatcher.find()) {
                // 动态里的评论
                oid = videoPatternMatcher.group(1);
                type = "1";
            }
        }

        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(oid)) {
            log.info("评论删除失败 [{}] {}", rpid, title);
            log.debug("{}", native_uri);
            throw new BusinessException("评论删除失败，未支持该类型：" + native_uri);
        }

        CommentDelParams delParams = new CommentDelParams(oid, rpid, type);
        if(deletedCache != null && deletedCache.contains(delParams.toCacheKey())){
            log.info("评论已删除 [{}] {}", rpid, title);
            return delParams;
        }

        String finalType = type;
        String finalOid = oid;
        ApiResult<JSONObject> apiResult = new ModifyApi<JSONObject>(client, user,
                "https://api.bilibili.com/x/v2/reply/del", JSONObject.class)
                .modify(new HashMap<String, String>() {{
                    put("oid", finalOid);
                    put("type", finalType);
                    put("rpid", rpid);
                }});
        if (apiResult.isFail()) {
            throw new ApiException(apiResult);
        }
        log.info("评论删除成功 [{}] {}", rpid, title);
        log.debug("{}", native_uri);
        return delParams;
    }

}
