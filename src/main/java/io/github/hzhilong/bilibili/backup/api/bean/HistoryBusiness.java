package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Getter;

/**
 * 历史记录业务类型
 *
 * @author hzhilong
 * @version 1.0
 */
public enum HistoryBusiness {
    ALL("all", "全部类型"),
    ARCHIVE("archive", "稿件"),
    PGC("pgc", "剧集（番剧/影视）"),
    LIVE("live", "直播"),
    ARTICLE_LIST("article-list", "文集"),
    ARTICLE("article", "文章");

    private final String code;
    @Getter
    private final String desc;

    HistoryBusiness(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
