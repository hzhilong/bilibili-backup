package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 简单的弹幕数据
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDM {
    private String progress;
    private String content;
    private String time;
    private int weight;
    private String midHash;
    private List<Long> uids;
    private List<Upper> users;

    public SimpleDM(String progress, String content, String time, int weight, String midHash) {
        this(progress, content, time, weight, midHash, null);
    }

    public SimpleDM(String progress, String content, String time, int weight, String midHash, List<Long> uids) {
        this.progress = progress;
        this.content = content;
        this.time = time;
        this.weight = weight;
        this.midHash = midHash;
        this.uids = uids;
    }

}
