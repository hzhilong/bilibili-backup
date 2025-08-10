package io.github.hzhilong.bilibili.backup.app.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.hzhilong.base.error.BusinessException;
import io.github.hzhilong.base.utils.StringUtils;
import io.github.hzhilong.bilibili.backup.app.state.GlobalState;
import io.github.hzhilong.bilibili.backup.app.state.setting.AppSettingItems;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hzhilong
 * @version 1.0
 */
@Slf4j
public class OpenAIUtils {

    public static String chat(OkHttpClient client, String msg) throws BusinessException {
        String url = AppSettingItems.OPENAI_API_URL.getValue();
        if (StringUtils.isEmpty(url)) {
            throw new BusinessException("配置为空");
        }
        String key = AppSettingItems.OPENAI_API_KEY.getValue();
        String model = AppSettingItems.OPENAI_API_MODEL.getValue();

        log.debug("OpenAI API URL：{}", url);
        log.debug("OpenAI API key：{}", key);
        log.debug("OpenAI API model：{}", model);
        log.debug("chat msg：{}", msg);

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", model);
        JSONArray msgList = new JSONArray();
        JSONObject msgObj = new JSONObject();
        msgObj.put("role", "user");
        msgObj.put("content", msg);
        msgList.add(msgObj);
        jsonBody.put("messages", msgList);

        String reqJson = jsonBody.toJSONString();
        log.debug("req json：{}", reqJson);
        RequestBody body = RequestBody.create(reqJson, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + key)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new BusinessException("调用AI出错，Unexpected code " + response);
            String rep = null;
            if (response.body() == null) {
                throw new BusinessException("调用AI失败，相应内容为空");
            } else {
                rep = response.body().string();
            }
            log.debug("chat rep：{}", rep);
            return parseRepContent(rep);
        } catch (IOException e) {
            throw new BusinessException("调用AI出错，Unexpected code " + e.getMessage());
        }
    }

    private static String parseRepContent(String repJsonStr) throws BusinessException {
        JSONObject repJson = JSONObject.parseObject(repJsonStr);
        try {
            return repJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (Exception e) {
            throw new BusinessException("调用AI出错，解析响应内容出错。");
        }
    }

    public static int askForAnswers(OkHttpClient client, String title, List<String> answers) throws BusinessException {
        StringBuilder sb = new StringBuilder();
        sb.append("【规则】\n" +
                "- 你必须且只能回答一个阿拉伯数字（0、1、2、3...）。\n" +
                "- 不允许输出任何其他内容（包括解释、标点、空格、换行等）。\n" +
                "- 即使不确定，也必须选择一个最可能的答案序号。\n" +
                "- 如果答案为[是否符合]，则代表的是该[标题]内容是否符合B站社区准则。哪怕没有违反社区准则，但只要是负面、抱怨、打击视频/UP 主体验的，就算“不符合”。\n" +
                "\n" +
                "【哔哩哔哩社区准则主要违规类型】\n" +
                "违法违规；人身攻击与辱骂；低俗及不当内容；引导不当行为；垃圾广告与推广；侵犯权益；恶意刷屏与扰乱。\n" +
                "\n" +
                "【标题】");
        sb.append(title);
        sb.append("\n" +
                "【答案】\n");
        for (int i = 0; i < answers.size(); i++) {
            sb.append(i);
            sb.append(". ");
            sb.append(answers.get(i));
            sb.append("\n");
        }
        return Integer.parseInt(chat(client, sb.toString()));
    }

    public static void main(String[] args) throws BusinessException {
        List<String> list = new ArrayList<>();
        list.add("哗啦哗啦");
        list.add("B站");
        System.out.printf("" + askForAnswers(GlobalState.CLIENT, "哔哩哔哩的英文名是bilibili，因而被简称为？", list));
    }

}
