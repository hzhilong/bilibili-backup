package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会员信息
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class VIP {

    /**
     * vipType : 1
     * vipDueDate : 1744819200000
     * dueRemark :
     * accessStatus : 0
     * vipStatus : 0
     * vipStatusWarn :
     * themeType : 0
     * avatar_subscript : 0
     * nickname_color :
     * avatar_subscript_url :
     */

    private Integer vipType;
    private Long vipDueDate;
    private String dueRemark;
    private Integer accessStatus;
    private Integer vipStatus;
    private String vipStatusWarn;
    private Integer themeType;
    private Integer avatarSubscript;
    private String nicknameColor;
    private String avatarSubscriptUrl;

}
