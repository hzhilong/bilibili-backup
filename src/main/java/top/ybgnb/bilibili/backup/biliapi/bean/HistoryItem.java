package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName HistoryItem
 * @Description 历史记录项
 * @Author hzhilong
 * @Time 2024/12/6
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryItem {

    /**
     * 目标id
     * 稿件视频&剧集（当`business=archive`或`business=pgc`时）：稿件avid
     * <br />直播（当`business=live`时）：直播间id
     * <br />文章（当`business=article`时）：文章cvid
     * <br />文集（当`business=article-list`时）：文集rlid
     */
    private Long oid;

    /**
     * 剧集epid
     * 仅用于剧集
     */
    private Long epid;

    /**
     * 稿件bvid
     * 仅用于稿件视频
     */
    private String bvid;

    /**
     * 观看到的视频分P数
     * 仅用于稿件视频
     */
    private Integer page;

    /**
     * 观看到的对象id
     * 稿件视频&剧集（当`business=archive`或`business=pgc`时）：视频cid
     * <br />文集（当`business=article-list`时）：文章cvid
     */
    private Long cid;

    /**
     * 观看到的视频分 P 标题
     * 仅用于稿件视频
     */
    private String part;

    /**
     * 业务类型
     */
    private HistoryBusiness business;

    /**
     * 记录查看的平台代码
     * 1 3 5 7：手机端<br />2：web端<br />4 6：pad端<br />33：TV端<br />0：其他
     */
    private Integer dt;

}
