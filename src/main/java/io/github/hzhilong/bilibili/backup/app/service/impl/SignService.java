package io.github.hzhilong.bilibili.backup.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import io.github.hzhilong.bilibili.backup.api.bean.ApiResult;
import io.github.hzhilong.bilibili.backup.api.request.BaseApi;
import io.github.hzhilong.bilibili.backup.app.service.BaseService;
import io.github.hzhilong.bilibili.backup.api.user.User;
import io.github.hzhilong.base.error.BusinessException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 签名相关
 *
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class SignService extends BaseService {

    public static int[] MIXIN_KEY_ENC_TAB = new int[]{
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
    };

    public SignService(OkHttpClient client, User user) {
        super(client, user);
    }

    public String getMixinKey() throws BusinessException {
        ApiResult<JSONObject> nav = new BaseApi<JSONObject>(client, user,
                "https://api.bilibili.com/x/web-interface/nav", false,
                JSONObject.class).apiGet();
        if (nav.isFail()) {
            throw new BusinessException(nav.getMessage());
        }
        JSONObject wbiImg = nav.getData().getJSONObject("wbi_img");
        String imgUrl = wbiImg.getString("img_url");
        String subUrl = wbiImg.getString("sub_url");
        String imgKey = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf("."));
        String subKey = subUrl.substring(subUrl.lastIndexOf("/") + 1, subUrl.lastIndexOf("."));
        String mixinKey = genMixinKey(imgKey, subKey);
//        log.info("获取mixinKey：{}", mixinKey);
        log.info("初始化接口调用，请稍候...");
        return mixinKey;
    }

    public String genMixinKey(String imgKey, String subKey) {
        String rawWbiKey = imgKey + subKey;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(rawWbiKey.charAt(MIXIN_KEY_ENC_TAB[i]));
        }
        return sb.toString();
    }

    private static final char[] hexDigits = "0123456789abcdef".toCharArray();

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            char[] result = new char[messageDigest.length * 2];
            for (int i = 0; i < messageDigest.length; i++) {
                result[i * 2] = hexDigits[(messageDigest[i] >> 4) & 0xF];
                result[i * 2 + 1] = hexDigits[messageDigest[i] & 0xF];
            }
            return new String(result);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String encodeURIComponent(Object o) {
        try {
            return URLEncoder.encode(o.toString(), "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String wbiSign(String mixinKey, Map<String, String> queryParams) {
        // 用TreeMap自动排序
        TreeMap<String, String> sortMap = new TreeMap<>(queryParams);
        sortMap.put("wts", String.valueOf(System.currentTimeMillis() / 1000));
        String param = sortMap.entrySet().stream()
                .map(it -> String.format("%s=%s", it.getKey(), encodeURIComponent(it.getValue())))
                .collect(Collectors.joining("&"));
        String s = param + mixinKey;
        return md5(s);
    }

}
