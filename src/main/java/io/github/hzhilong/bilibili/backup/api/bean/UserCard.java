package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户名片信息
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class UserCard {


    /**
     * card : {"mid":"3546812940027964","name":"bili_3546812940027964","approve":false,"sex":"保密","rank":"10000","face":"https://i1.hdslb.com/bfs/face/30eebd1810dd75e118d1f9185abad3cf57ec8525.jpg","face_nft":0,"face_nft_type":0,"DisplayRank":"0","regtime":0,"spacesta":-2,"birthday":"","place":"","description":"","article":0,"attentions":[],"fans":34,"friend":3941,"attention":3941,"sign":"","level_info":{"current_level":2,"current_min":0,"current_exp":0,"next_exp":0},"pendant":{"pid":0,"name":"","image":"","expire":0,"image_enhance":"","image_enhance_frame":"","n_pid":0},"nameplate":{"nid":0,"name":"","image":"","image_small":"","level":"","condition":""},"Official":{"role":0,"title":"","desc":"","type":-1},"official_verify":{"type":-1,"desc":""},"vip":{"type":0,"status":0,"due_date":0,"vip_pay_type":0,"theme_type":0,"label":{"path":"","text":"","label_theme":"","text_color":"","bg_style":0,"bg_color":"","border_color":"","use_img_label":true,"img_label_uri_hans":"","img_label_uri_hant":"","img_label_uri_hans_static":"https://i0.hdslb.com/bfs/vip/d7b702ef65a976b20ed854cbd04cb9e27341bb79.png","img_label_uri_hant_static":"https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/KJunwh19T5.png"},"avatar_subscript":0,"nickname_color":"","role":0,"avatar_subscript_url":"","tv_vip_status":0,"tv_vip_pay_type":0,"tv_due_date":0,"avatar_icon":{"icon_resource":{}},"vipType":0,"vipStatus":0},"is_senior_member":0,"name_render":null}
     * following : false
     * archive_count : 0
     * article_count : 0
     * follower : 34
     * like_num : 0
     */

    private CardDTO card;
    private Boolean following;
    private Integer archiveCount;
    private Integer articleCount;
    private Integer follower;
    private Integer likeNum;

    @NoArgsConstructor
    @Data
    public static class CardDTO {
        /**
         * mid : 3546812940027964
         * name : bili_3546812940027964
         * approve : false
         * sex : 保密
         * rank : 10000
         * face : https://i1.hdslb.com/bfs/face/30eebd1810dd75e118d1f9185abad3cf57ec8525.jpg
         * face_nft : 0
         * face_nft_type : 0
         * DisplayRank : 0
         * regtime : 0
         * spacesta : -2
         * birthday :
         * place :
         * description :
         * article : 0
         * attentions : []
         * fans : 34
         * friend : 3941
         * attention : 3941
         * sign :
         * level_info : {"current_level":2,"current_min":0,"current_exp":0,"next_exp":0}
         * pendant : {"pid":0,"name":"","image":"","expire":0,"image_enhance":"","image_enhance_frame":"","n_pid":0}
         * nameplate : {"nid":0,"name":"","image":"","image_small":"","level":"","condition":""}
         * Official : {"role":0,"title":"","desc":"","type":-1}
         * official_verify : {"type":-1,"desc":""}
         * vip : {"type":0,"status":0,"due_date":0,"vip_pay_type":0,"theme_type":0,"label":{"path":"","text":"","label_theme":"","text_color":"","bg_style":0,"bg_color":"","border_color":"","use_img_label":true,"img_label_uri_hans":"","img_label_uri_hant":"","img_label_uri_hans_static":"https://i0.hdslb.com/bfs/vip/d7b702ef65a976b20ed854cbd04cb9e27341bb79.png","img_label_uri_hant_static":"https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/KJunwh19T5.png"},"avatar_subscript":0,"nickname_color":"","role":0,"avatar_subscript_url":"","tv_vip_status":0,"tv_vip_pay_type":0,"tv_due_date":0,"avatar_icon":{"icon_resource":{}},"vipType":0,"vipStatus":0}
         * is_senior_member : 0
         * name_render : null
         */

        private String mid;
        private String name;
        private Boolean approve;
        private String sex;
        private String rank;
        private String face;
        private Integer faceNft;
        private Integer faceNftType;
        private String DisplayRank;
        private Integer regtime;
        private Integer spacesta;
        private String birthday;
        private String place;
        private String description;
        private Integer article;
        private Integer fans;
        private Integer friend;
        private Integer attention;
        private String sign;
        private LevelInfoDTO levelInfo;
        private PendantDTO pendant;
        private NameplateDTO nameplate;
        private OfficialDTO Official;
        private OfficialVerifyDTO officialVerify;
        private VipDTO vip;
        private Integer isSeniorMember;
        private Object nameRender;
        private List<?> attentions;

        @NoArgsConstructor
        @Data
        public static class LevelInfoDTO {
            /**
             * current_level : 2
             * current_min : 0
             * current_exp : 0
             * next_exp : 0
             */

            private Integer currentLevel;
            private Integer currentMin;
            private Integer currentExp;
            private Integer nextExp;
        }

        @NoArgsConstructor
        @Data
        public static class PendantDTO {
            /**
             * pid : 0
             * name :
             * image :
             * expire : 0
             * image_enhance :
             * image_enhance_frame :
             * n_pid : 0
             */

            private Integer pid;
            private String name;
            private String image;
            private Integer expire;
            private String imageEnhance;
            private String imageEnhanceFrame;
            private Long nPid;
        }

        @NoArgsConstructor
        @Data
        public static class NameplateDTO {
            /**
             * nid : 0
             * name :
             * image :
             * image_small :
             * level :
             * condition :
             */

            private Integer nid;
            private String name;
            private String image;
            private String imageSmall;
            private String level;
            private String condition;
        }

        @NoArgsConstructor
        @Data
        public static class OfficialDTO {
            /**
             * role : 0
             * title :
             * desc :
             * type : -1
             */

            private Integer role;
            private String title;
            private String desc;
            private Integer type;
        }

        @NoArgsConstructor
        @Data
        public static class OfficialVerifyDTO {
            /**
             * type : -1
             * desc :
             */

            private Integer type;
            private String desc;
        }

        @NoArgsConstructor
        @Data
        public static class VipDTO {
            /**
             * type : 0
             * status : 0
             * due_date : 0
             * vip_pay_type : 0
             * theme_type : 0
             * label : {"path":"","text":"","label_theme":"","text_color":"","bg_style":0,"bg_color":"","border_color":"","use_img_label":true,"img_label_uri_hans":"","img_label_uri_hant":"","img_label_uri_hans_static":"https://i0.hdslb.com/bfs/vip/d7b702ef65a976b20ed854cbd04cb9e27341bb79.png","img_label_uri_hant_static":"https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/KJunwh19T5.png"}
             * avatar_subscript : 0
             * nickname_color :
             * role : 0
             * avatar_subscript_url :
             * tv_vip_status : 0
             * tv_vip_pay_type : 0
             * tv_due_date : 0
             * avatar_icon : {"icon_resource":{}}
             * vipType : 0
             * vipStatus : 0
             */

            private Integer type;
            private Integer status;
            private Long dueDate;
            private Integer vipPayType;
            private Integer themeType;
            private LabelDTO label;
            private Integer avatarSubscript;
            private String nicknameColor;
            private Integer role;
            private String avatarSubscriptUrl;
            private Integer tvVipStatus;
            private Integer tvVipPayType;
            private Long tvDueDate;
            private AvatarIconDTO avatarIcon;
            private Integer vipType;
            private Integer vipStatus;

            @NoArgsConstructor
            @Data
            public static class LabelDTO {
                /**
                 * path :
                 * text :
                 * label_theme :
                 * text_color :
                 * bg_style : 0
                 * bg_color :
                 * border_color :
                 * use_img_label : true
                 * img_label_uri_hans :
                 * img_label_uri_hant :
                 * img_label_uri_hans_static : https://i0.hdslb.com/bfs/vip/d7b702ef65a976b20ed854cbd04cb9e27341bb79.png
                 * img_label_uri_hant_static : https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/KJunwh19T5.png
                 */

                private String path;
                private String text;
                private String labelTheme;
                private String textColor;
                private Integer bgStyle;
                private String bgColor;
                private String borderColor;
                private Boolean useImgLabel;
                private String imgLabelUriHans;
                private String imgLabelUriHant;
                private String imgLabelUriHansStatic;
                private String imgLabelUriHantStatic;
            }

            @NoArgsConstructor
            @Data
            public static class AvatarIconDTO {
                /**
                 * icon_resource : {}
                 */

                private IconResourceDTO iconResource;

                @NoArgsConstructor
                @Data
                public static class IconResourceDTO {
                }
            }
        }
    }
}
