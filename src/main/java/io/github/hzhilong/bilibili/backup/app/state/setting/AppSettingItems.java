package io.github.hzhilong.bilibili.backup.app.state.setting;

import io.github.hzhilong.baseapp.state.setting.AppSettingItem;
import io.github.hzhilong.baseapp.state.setting.value.BooleanItemValue;
import io.github.hzhilong.baseapp.state.setting.value.StringItemValue;

/**
 * 应用设置项
 *
 * @author hzhilong
 * @version 1.0
 */
public class AppSettingItems {

    public static AppSettingItem<Boolean> ALLOW_FAILURE
            = new AppSettingItem<>("setting.allow-failure", new BooleanItemValue(false),
            "【还原】：还原失败时，继续还原下一个数据");
    public static AppSettingItem<Boolean> DIRECT_RESTORE
            = new AppSettingItem<>("setting.direct-restore", new BooleanItemValue(false),
            "【还原】：忽略新账号现有的数据，直接还原");
    public static AppSettingItem<Boolean> FAV_SAVE_TO_DEFAULT_ON_FAILURE
            = new AppSettingItem<>("setting.fav-save-to-default-on-failure", new BooleanItemValue(false),
            "【还原】：创建[收藏夹]达到上限后，将该收藏夹的视频移入默认收藏夹");
    public static AppSettingItem<Boolean> SELECT_RELATION_TAG
            = new AppSettingItem<>("setting.select-relation-tag", new BooleanItemValue(false),
            "【备份/还原】：手动选择需要操作的[关注分组]");
    public static AppSettingItem<Boolean> SELECT_FAV
            = new AppSettingItem<>("setting.select-fav", new BooleanItemValue(false),
            "【备份/还原】：手动选择需要操作的[收藏夹]（不支持分段处理）");

    public static AppSettingItem<String> OPENAI_API_URL
            = new AppSettingItem<>("setting.openai-api-url", new StringItemValue(""),
            "【答题】：OpenAI API URL");

    public static AppSettingItem<String> OPENAI_API_KEY
            = new AppSettingItem<>("setting.openai-api-key", new StringItemValue(""),
            "【答题】：OpenAI API 密匙");

    public static AppSettingItem<String> OPENAI_API_MODEL
            = new AppSettingItem<>("setting.openai-api-model", new StringItemValue(""),
            "【答题】：OpenAI API 模型");

    public static AppSettingItem<Boolean> AUTO_SUBMIT_ANSWER
            = new AppSettingItem<>("setting.auto-submit-answer", new BooleanItemValue(false),
            "【答题】：分数达到60分后自动提交");

}
