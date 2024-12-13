package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 历史记录
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class History {

    private String title;
    private String cover;

    /**
     * 重定向 url
     * 仅用于剧集和直播
     */
    private String uri;

    private HistoryItem history;

    /**
     * 视频分 P 数目
     * 仅用于稿件视频
     */
    private Integer videos;

    private String authorName;

    private Long authorMid;

    /**
     * 查看时间
     * 时间戳
     */
    private Long viewAt;

    /**
     * 视频观看进度
     * 单位为秒<br />用于稿件视频或剧集
     */
    private Integer progress;

    /**
     * 分 P 标题
     * 用于稿件视频或剧集
     */
    private String showTitle;

    /**
     * 视频总时长
     * 用于稿件视频或剧集
     */
    private Integer duration;

    /**
     * 条目目标 id
     */
    private Long kid;

}
