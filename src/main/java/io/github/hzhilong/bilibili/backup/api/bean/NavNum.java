package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class NavNum {

    /**
     * video : 172
     * bangumi : 0
     * cinema : 0
     * channel : {"master":0,"guest":0}
     * favourite : {"master":0,"guest":0}
     * tag : 0
     * article : 0
     * playlist : 0
     * album : 2
     * audio : 0
     * pugv : 0
     * season_num : 0
     * opus : 2
     */

    private Integer video;
    private Integer bangumi;
    private Integer cinema;
    private ChannelDTO channel;
    private FavouriteDTO favourite;
    private Integer tag;
    private Integer article;
    private Integer playlist;
    private Integer album;
    private Integer audio;
    private Integer pugv;
    private Integer seasonNum;
    private Integer opus;

    @NoArgsConstructor
    @Data
    public static class ChannelDTO {
        /**
         * master : 0
         * guest : 0
         */

        private Integer master;
        private Integer guest;
    }

    @NoArgsConstructor
    @Data
    public static class FavouriteDTO {
        /**
         * master : 0
         * guest : 0
         */

        private Integer master;
        private Integer guest;
    }
}
