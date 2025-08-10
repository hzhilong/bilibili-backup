package io.github.hzhilong.bilibili.backup.app.service.impl;

import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.bilibili.backup.api.bean.AnswerStatus;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.bean.Question;
import io.github.hzhilong.bilibili.backup.api.bean.QuestionCheckResult;
import io.github.hzhilong.bilibili.backup.api.bean.QuestionData;
import io.github.hzhilong.bilibili.backup.api.request.AddQueryParams;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.api.request.ModifyApi;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.error.ApiException;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 答题
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class AnswerService extends BaseService {

    public AnswerService(OkHttpClient client, User user) {
        super(client, user);
    }

    @Override
    protected void handleInterrupt() throws BusinessException {
        super.handleInterrupt();
    }

    /**
     * 获取答题状态
     */
    public AnswerStatus getAnswerStatus() throws BusinessException {
        ApiResult<AnswerStatus> apiResult = new BaseApi<AnswerStatus>(client, user,
                "https://api.bilibili.com/x/answer/v4/status",
                new AddQueryParams() {
                    @Override
                    public void addQueryParams(Map<String, String> queryParams) {
                        queryParams.put("re_src", "0");
                        queryParams.put("web_location", "333.858");
                    }
                }, true, AnswerStatus.class).apiGet();
        if (apiResult.isFail()) {
            throw new ApiException(apiResult);
        }
        return apiResult.getData();
    }

    /**
     * 拉取题目
     */
    public Question pullQuestion(AnswerStatus status) throws BusinessException {
        if ("base".equals(status.getStage())) {
            log.info("正在拉取基础题...");
            return pullQuestion("https://api.bilibili.com/x/answer/v4/base");
        } else if ("extra".equals(status.getStage())) {
            log.info("正在拉取附加题...");
            return pullQuestion("https://api.bilibili.com/x/answer/v4/extra");
        } else {
            throw new BusinessException("当前工具咱不支持自选题");
        }
    }

    public Question pullQuestion(String url) throws BusinessException {
        BaseApi<QuestionData> api = new BaseApi<>(client, user, url, new AddQueryParams() {
            @Override
            public void addQueryParams(Map<String, String> queryParams) {
                queryParams.put("platform", "pc");
                queryParams.put("image_version", "v");
                queryParams.put("re_src", "0");
                queryParams.put("web_location", "333.858");
            }
        }, true, QuestionData.class);
        ApiResult<QuestionData> apiResult = api.apiGet();
        if (apiResult.isFail()) {
            throw new ApiException(apiResult);
        }
        return apiResult.getData().getQuestion();
    }

    /**
     * 提交题目（基础题/附加题）
     */
    public QuestionCheckResult checkQuestion(String stage, int questionId, String ansHash) throws BusinessException {
        HashMap<String, String> params = new HashMap<>();
        params.put("question_id", String.valueOf(questionId));
        if ("extra".equals(stage)) {
            params.put("ans_key", ansHash);
        } else {
            params.put("ans_hash", ansHash);
        }
        params.put("re_src", "0");
        ApiResult<QuestionCheckResult> apiResult = new ModifyApi<QuestionCheckResult>(client, user,
                "https://api.bilibili.com/x/answer/v4/base/check", QuestionCheckResult.class)
                .modify(params);
        if (apiResult.isFail()) {
            if (apiResult.getCode() == 41096) {
                log.info("出现验证码，请使用APP/网页前往答题页面，手动回答一题，完成验证码操作再回到软件继续。");
            }
            throw new ApiException(apiResult);
        }
        return apiResult.getData();
    }

    /**
     * 提交试卷
     */
    public Object submit() throws BusinessException {
        ApiResult<Object> apiResult = new ModifyApi<>(client, user,
                "https://api.bilibili.com/x/answer/v4/submit", Object.class)
                .modify(null);
        if (apiResult.isFail()) {
            throw new ApiException(apiResult);
        }
        return apiResult.getData();
    }
}
