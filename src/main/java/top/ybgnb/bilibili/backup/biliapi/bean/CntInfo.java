package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CntInfo
 * @Description 状态数
 * @Author hzhilong
 * @Time 2024/9/23
 * @Version 1.0
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
    private int thumb_up;
}
