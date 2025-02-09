package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户名片信息
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCard {


    /**
     * card : {"mid":"1","name":"bishi","sex":"男","face":"http://i0.hdslb.com/bfs/face/34c5b30a990c7ce4a809626d8153fa7895ec7b63.gif","spacesta":0,"fans":191666,"friend":6,"attention":6,"level_info":{"current_level":6}}
     * archive_count : 2
     * follower : 191666
     * like_num : 128009
     */

    private CardDTO card;
    private Integer archiveCount;
    private Integer follower;
    private Integer likeNum;

    @NoArgsConstructor
    @Data
    public static class CardDTO {
        /**
         * mid : 1
         * name : bishi
         * sex : 男
         * face : http://i0.hdslb.com/bfs/face/34c5b30a990c7ce4a809626d8153fa7895ec7b63.gif
         * spacesta : 0
         * fans : 191666
         * friend : 6
         * attention : 6
         * level_info : {"current_level":6}
         */

        private String mid;
        private String name;
        private String sex;
        private String face;
        private Integer spacesta;
        private Integer fans;
        private Integer friend;
        private Integer attention;
        private LevelInfoDTO levelInfo;

        @NoArgsConstructor
        @Data
        public static class LevelInfoDTO {
            /**
             * current_level : 6
             */

            private Integer currentLevel;
        }
    }
}
