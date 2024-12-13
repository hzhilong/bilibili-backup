package io.github.hzhilong.bilibili.backup.api.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 状态数
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class CntInfo {

    /**
     * 收藏数
     */
    private int collect;

    /**
     * 播放数
     */
    private int play;

    /**
     * 弹幕数
     */
    private int danmaku;

    /**
     * 分享数
     */
    private int share;

    /**
     * 点赞数
     */
    @JSONField(name = "thumb_up")
    private int thumbUp;
}
