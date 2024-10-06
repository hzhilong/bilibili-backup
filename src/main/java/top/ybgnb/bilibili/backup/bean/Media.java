package top.ybgnb.bilibili.backup.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Media
 * @Description
 * @Author hzhilong
 * @Time 2024/9/23
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class Media {

    /**
     * id : 113045854553938
     * type : 2
     * title : 只需一招，轻松下载国家中小学智慧教育平台的电子课本
     * cover : http://i2.hdslb.com/bfs/archive/907ebbe82b62cde36bfd7cf8495e91a5eb8ddd48.jpg
     * intro : 这是一个完全由本人从0开发的程序，欢迎提出改进建议。
     本程序基于VB .net实现，不是Python!，已在Github开源。
     下载地址：
     123网盘：https://www.123pan.com/s/H1NUVv-Zpphd   提取码:smdw
     蓝奏云：https://cjhact.lanzoul.com/b00cri9z1e    密码:bxxj
     本软件支持解析下载智慧中小学平台的电子课本以及课程资源，支持登录和免登录下载两种模式，同时支持批量解析、下载电子课本链接。
     Github项目链接：http
     * page : 1
     * duration : 389
     * upper : {"mid":3493081181588076,"name":"山中繁星","face":"https://i0.hdslb.com/bfs/face/f8a5bf924a607e47fc6818aa094452c0156fe233.jpg"}
     * attr : 0
     * cnt_info : {"collect":1563,"play":13193,"danmaku":1,"vt":0,"play_switch":0,"reply":0,"view_text_1":"1.3万"}
     * link : bilibili://video/113045854553938
     * ctime : 1724944496
     * pubtime : 1724944496
     * fav_time : 1726657583
     * bv_id : BV1MpHgesEYu
     * bvid : BV1MpHgesEYu
     * media_list_link : bilibili://music/playlist/playpage/4299718035?page_type=3&oid=113045854553938&otype=2
     */

    private Long id;
    private Integer type;
    private String title;
    private String cover;
    private String intro;
    private Integer page;
    private Integer duration;
    private Upper upper;
    private Integer attr;
    private CntInfo cntInfo;
    private String link;
    private Integer ctime;
    private Integer pubtime;
    private Integer favTime;
    private String bvId;
    private String bvid;

}
