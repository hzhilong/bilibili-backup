package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频分P
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class VideoPart {

    /**
     * duration : 194
     * vid :
     * part : 一代人有一代人的射雕！独属于华夏的英雄情怀！
     * weblink :
     * from : vupload
     * page : 1
     * dimension : {"rotate":0,"width":1920,"height":1080}
     * cid : 28153349848
     * first_frame : http://i1.hdslb.com/bfs/storyff/n250130sa3xktygis4cfy1468znzbefq_firsti.jpg
     */

    private Integer duration;
    private String bvid;
    private String vid;
    private String part;
    private String weblink;
    private String from;
    private Integer page;
    private DimensionDTO dimension;
    private Long cid;
    private String firstFrame;

    @NoArgsConstructor
    @Data
    public static class DimensionDTO {
        /**
         * rotate : 0
         * width : 1920
         * height : 1080
         */

        private Integer rotate;
        private Integer width;
        private Integer height;
    }
}
