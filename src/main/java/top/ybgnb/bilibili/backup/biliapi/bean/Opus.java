package top.ybgnb.bilibili.backup.biliapi.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Opus
 * @Description 专栏
 * @Author hzhilong
 * @Time 2024/9/23
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class Opus {

    /**
     * jump_url : //www.bilibili.com/opus/737654667569266705
     * opus_id : 737654667569266705
     * time_text : 收藏于6分钟前
     * title : BAT脚本 获取管理员权限/提权
     */

    private String jumpUrl;
    private Long opusId;
    private String timeText;
    private String title;
}
