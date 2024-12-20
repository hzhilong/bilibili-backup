package io.github.hzhilong.bilibili.backup.api.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.bilibili.backup.app.service.impl.SignService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 接口请求基类
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class BaseApi<D> implements AddQueryParams {
    /**
     * 最长的延迟时间 毫秒
     */
    public final static int MAX_DELAY_TIME = 5 * 1000;

    protected OkHttpClient client;

    protected User user;

    protected String url;

    protected String requestUrl;

    @Setter
    protected AddQueryParams addQueryParams;

    protected Class<?>[] dataClasses;

    protected boolean isWbiSign;

    protected static String MIXIN_KEY;

    private SignService signService;

    private final Random random;

    public BaseApi(OkHttpClient client, User user, String url, boolean isWbiSign, Class<?>... dataClasses) {
        this(client, user, url, null, isWbiSign, dataClasses);
    }

    public BaseApi(OkHttpClient client, User user, String url, AddQueryParams addQueryParams, boolean isWbiSign, Class<?>... dataClasses) {
        this.client = client;
        this.user = user;
        this.url = url;
        this.addQueryParams = addQueryParams;
        if (user == null) {
            this.isWbiSign = false;
        } else {
            this.isWbiSign = isWbiSign;
        }
        this.dataClasses = dataClasses;
        if (isWbiSign) {
            signService = new SignService(client, user);
        }
        this.random = new Random();
    }

    protected Request getGETRequest(String url, Map<String, String> queryParams) {
        if (queryParams != null) {
            StringBuilder urlBuilder = new StringBuilder(url);
            boolean flag = false;
            if (!url.contains("&") && !url.contains("?")) {
                urlBuilder.append("?");
                flag = true;
            }
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                if (flag) {
                    urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                    flag = false;
                } else {
                    urlBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            url = urlBuilder.toString();
        }
        Request.Builder builder = new Request.Builder()
                .url(url);
        this.requestUrl = url;
        addBaseHeader(builder, url);
        return builder.build();
    }

    protected Request getPOSTRequest(String url, Map<String, String> queryParams, Map<String, String> formParams, String jsonBody) {
        if (StringUtils.notEmpty(jsonBody)) {
            log.debug("post内容：{}", jsonBody);
        } else {
            log.debug("post表单：{}", formParams);
        }
        if (queryParams != null) {
            StringBuilder urlBuilder = new StringBuilder(url);
            boolean flag = false;
            if (!url.contains("&") && !url.contains("?")) {
                urlBuilder.append("?");
                flag = true;
            }
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                if (flag) {
                    urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                    flag = false;
                } else {
                    urlBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            url = urlBuilder.toString();
        }
        Request.Builder builder;
        if (StringUtils.isEmpty(jsonBody)) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (formParams != null && !formParams.isEmpty()) {
                formParams.forEach((key, value) -> formBodyBuilder.add(key, value == null ? "" : value));
            }
            builder = new Request.Builder()
                    .url(url).post(formBodyBuilder.build());
        } else {
            builder = new Request.Builder()
                    .url(url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody));
        }
        this.requestUrl = url;
        addBaseHeader(builder, url);
        return builder.build();
    }

    protected void addBaseHeader(Request.Builder builder, String url) {
        builder.addHeader(":authority", "api.bilibili.com");
//        builder.addHeader(":method", "GET");
        builder.addHeader(":path", url.substring(url.indexOf(".com/") + 4));
        builder.addHeader(":scheme", "https");
        builder.addHeader("accept", "*/*");
//        builder.addHeader("accept-encoding", "gzip, deflate, br, zstd");
        builder.addHeader("accept-language", "zh-CN,zh;q=0.9");
        if (this.user != null) {
            builder.addHeader("cookie", user.getCookie());
        }
        builder.addHeader("origin", "https://space.bilibili.com");
        builder.addHeader("priority", "u=1, i");
        if (this.user != null) {
            builder.addHeader("referer", "https://space.bilibili.com/" + user.getUid() + "/fans/follow?spm_id_from=333.1365.0.0");
        } else {
            builder.addHeader("referer", "https://www.bilibili.com/?spm_id_from=333.999.0.0");
        }
        builder.addHeader("sec-ch-ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"");
        builder.addHeader("sec-ch-ua-mobile", "?0");
        builder.addHeader("sec-ch-ua-platform", "\"Windows\"");
        builder.addHeader("sec-fetch-dest", "empty");
        builder.addHeader("sec-fetch-mode", "cors");
        builder.addHeader("sec-fetch-site", "same-site");
        builder.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36");
    }

    public Request getRequest(Map<String, String> formParams) throws BusinessException {
        return getRequest(formParams, null);
    }

    public Request getRequest(String jsonBody) throws BusinessException {
        return getRequest(null, jsonBody);
    }

    public Request getRequest(Map<String, String> formParams, String jsonBody) throws BusinessException {
        Map<String, String> queryParams = new HashMap<>();
        addQueryParams(queryParams);
        if (this.addQueryParams != null) {
            this.addQueryParams.addQueryParams(queryParams);
        }
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            try {
                queryParams.put(entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new BusinessException("请求参数值编码失败：" + JSON.toJSONString(entry.getValue()));
            }
        }
        if (this.user != null) {
            queryParams.put("gaia_source", "main_web");
            if (isWbiSign && signService != null) {
                if (StringUtils.isEmpty(MIXIN_KEY)) {
                    MIXIN_KEY = signService.getMixinKey();
                }
                queryParams.put("w_rid", signService.wbiSign(MIXIN_KEY, queryParams));
            }
        }
        log.debug("请求参数：{}", queryParams);
        if (formParams == null && StringUtils.isEmpty(jsonBody)) {
            return getGETRequest(this.url, queryParams);
        } else {
            return getPOSTRequest(this.url, queryParams, formParams, jsonBody);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<D> {
        private Headers headers;
        private String body;
        private ApiResult<D> apiResult;
    }

    public ApiResponse<D> apiRequest(Map<String, String> formParams, boolean isParseBody) throws BusinessException {
        return apiRequest(formParams, null, isParseBody);
    }

    public ApiResponse<D> apiRequest(Map<String, String> formParams, String jsonBody, boolean isParseBody) throws BusinessException {
        Call call = client.newCall(this.getRequest(formParams, jsonBody));
        try (Response response = call.execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                log.debug("响应：({})", result);
                if (!StringUtils.isEmpty(result)) {
                    ApiResponse<D> apiResponse = new ApiResponse<>();
                    apiResponse.setBody(result);
                    apiResponse.setHeaders(response.headers());
                    if (isParseBody) {
                        apiResponse.setApiResult(parseApiResult(this.dataClasses, result));
                    }
                    return apiResponse;
                } else {
                    log.error("响应为空({})", response.code());
                    throw new BusinessException(String.format("响应为空(%s)", response.code()));
                }
            } else {
                log.debug("请求失败，code：{}", response.code());
                throw new BusinessException(String.format("请求失败，code：%s", response.code()));
            }
        } catch (IOException e) {
            log.error("请求出错", e);
            throw new BusinessException("请求出错");
        }
    }

    public ApiResponse<D> apiGetWithResponse() throws BusinessException {
        log.debug("【apiGetResponse】url：{}", url);
        return apiRequest(null, true);
    }

    public ApiResult<D> apiGet() throws BusinessException {
        log.debug("【apiGet】url：{}", url);
        return apiRequest(null, true).getApiResult();
    }

    public ApiResult<D> apiPost(Map<String, String> formParams) throws BusinessException {
        log.debug("【apiPost】url：{}", url);
        return apiRequest(formParams, true).getApiResult();
    }

    public ApiResult<D> apiPost(String jsonBody) throws BusinessException {
        log.debug("【apiPost】url：{}", url);
        return apiRequest(null, jsonBody, true).getApiResult();
    }

    public ApiResponse<D> htmlGet() throws BusinessException {
        log.debug("【htmlGet】url：{}", url);
        return apiRequest(null, false);
    }

    private static <D> ApiResult<D> parseApiResult(Class<?>[] dataClasses, String result) {
        ParameterizedTypeImpl outer = getParameterizedType(dataClasses);
        return JSON.parseObject(result, outer);
    }

    @NotNull
    private static ParameterizedTypeImpl getParameterizedType(Class<?>[] dataClasses) {
        ParameterizedTypeImpl outer = null;
        if (dataClasses == null || dataClasses.length == 0) {
            outer = new ParameterizedTypeImpl(new Type[]{Void.class}, null, ApiResult.class);
        } else if (dataClasses.length == 1) {
            outer = new ParameterizedTypeImpl(new Type[]{dataClasses[0]}, null, ApiResult.class);
        } else {
            for (int i = dataClasses.length - 1; i >= 0; i--) {
                if (i == dataClasses.length - 1) {
                    outer = new ParameterizedTypeImpl(new Type[]{dataClasses[i]}, null, dataClasses[i - 1]);
                    i--;
                } else {
                    outer = new ParameterizedTypeImpl(new Type[]{outer}, null, dataClasses[i]);
                }
            }
            outer = new ParameterizedTypeImpl(new Type[]{outer}, null, ApiResult.class);
        }
        return outer;
    }

    @Override
    public void addQueryParams(Map<String, String> queryParams) {

    }

    protected void sleep(int page) {
        try {
            Thread.sleep((1000 + 1000 * Integer.toString(page).length() + random.nextInt(3000)) % MAX_DELAY_TIME);
        } catch (InterruptedException ignored) {
        }
    }
}
