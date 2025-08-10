package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 答题题目
 *
 * @author hzhilong
 * @version 1.0
 */

@NoArgsConstructor
@Data
public class Question {

    /**
     * id : 1411
     * number : 1
     * q_height : 38.4
     * q_coord_y : 0
     * image : https://i0.hdslb.com/bfs/member/b099427e3773663f015a2ad4a634ec89.png
     * from :
     * options : [{"number":1,"high":42,"coord_y":38.4,"hash":"97472cf0cc5595b0588bc71323d6e9b9"},{"number":2,"high":42,"coord_y":80.4,"hash":"a0a3acf422d0dea0a56d973e6c2c2c46"}]
     * type_id : 0
     * type_name :
     * type_image :
     * question : {"title":"pfbvzhVOusArnZwdo1Xt+i/zQvEWpOGq1CQPzLTaVvyqqDPY97275V5JrT0SJW/jhP3kkFlCDVyLDxh3D6I6ng==","ans":[{"key":"97472cf0cc5595b0588bc71323d6e9b9","title":"EMUIRP2SR8InALbPuhdwJQ=="},{"key":"a0a3acf422d0dea0a56d973e6c2c2c46","title":"KoSKPobmj9dUX2rQ6+1eVA=="}]}
     * decision_ctx : {}
     */

    private int id;
    private int number;
    private double q_height;
    private int q_coord_y;
    private String image;
    private String from;
    private int type_id;
    private String type_name;
    private String type_image;
    private QuestionBean question;
    private DecisionCtxBean decision_ctx;
    private List<OptionsBean> options;

    @NoArgsConstructor
    @Data
    public static class QuestionBean {
        /**
         * title : pfbvzhVOusArnZwdo1Xt+i/zQvEWpOGq1CQPzLTaVvyqqDPY97275V5JrT0SJW/jhP3kkFlCDVyLDxh3D6I6ng==
         * ans : [{"key":"97472cf0cc5595b0588bc71323d6e9b9","title":"EMUIRP2SR8InALbPuhdwJQ=="},{"key":"a0a3acf422d0dea0a56d973e6c2c2c46","title":"KoSKPobmj9dUX2rQ6+1eVA=="}]
         */

        private String title;
        private List<AnsBean> ans;

        @NoArgsConstructor
        @Data
        public static class AnsBean {
            /**
             * key : 97472cf0cc5595b0588bc71323d6e9b9
             * title : EMUIRP2SR8InALbPuhdwJQ==
             */

            private String key;
            private String title;
        }
    }

    @NoArgsConstructor
    @Data
    public static class DecisionCtxBean {
    }

    @NoArgsConstructor
    @Data
    public static class OptionsBean {
        /**
         * number : 1
         * high : 42
         * coord_y : 38.4
         * hash : 97472cf0cc5595b0588bc71323d6e9b9
         */

        private int number;
        private int high;
        private double coord_y;
        private String hash;
    }

}
