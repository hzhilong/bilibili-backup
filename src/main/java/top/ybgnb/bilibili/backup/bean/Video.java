package top.ybgnb.bilibili.backup.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Video
 * @Description
 * @Author hzhilong
 * @Time 2024/9/23
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class Video {

    /**
     * aid : 113090163115091
     * bvid : BV13DHQejEdS
     * pic : http://i2.hdslb.com/bfs/archive/84ddfd9352a0058d75836eb92ff1b81f46ae22a1.jpg
     * title : 黑神话炸出多少电脑小白？帧数低就是游戏垃圾？短视频“鉴赏”官38
     * pubdate : 1725626700
     * duration : 404
     * owner : {"mid":6754449,"name":"健康绿色上网","face":"https://i0.hdslb.com/bfs/face/24c6effe26daf8aa4d15bf50f0f88aa2fa831533.jpg"}
     */

    private Long aid;
    private String bvid;
    private String pic;
    private String title;
    private Integer pubdate;
    private Integer duration;
    private Upper owner;

}
