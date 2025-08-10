package io.github.hzhilong.bilibili.backup.api.bean;

import io.github.hzhilong.base.error.BusinessException;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 答题状态
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class AnswerStatus {

    /**
     * hid : 1623207905520705
     * mid : 293793435
     * score : 2
     * status : 2
     * number : 2
     * result : failed
     * stage : base
     * version : v4
     * start_time : 1623207905
     * first_answer : 2
     * progress : 3
     * text : 继续答题
     * url : https://www.bilibili.com/h5/newbie/entry?navhide=1
     * in_reg_audit : false
     * edition : 0
     * rewards : null
     */

    private long hid;
    private long mid;
    private int score;
    private int status;
    private int number;
    private String result;
    private String stage;
    private String version;
    private long start_time;
    private long first_answer;
    private String progress;
    private String text;
    private String url;
    private boolean in_reg_audit;
    private int edition;


    public String _getStatusDesc() throws BusinessException {
        if (status == 0) {
            return "未答题";
        } else if (status == 2) {
            return "答题中";
        } else if (status == 3) {
            return "已通过";
        } else {
            throw new BusinessException("未知的答题状态：" + status);
        }
    }


    public String _getStageDesc() throws BusinessException {
        if ("base".equals(stage)) {
            return "基础题";
        } else if ("extra".equals(stage)) {
            return "附加题";
        } else if ("pro_type".equals(stage)) {
            return "等待选择自选题类型";
        } else if ("pro".equals(stage)) {
            return "自选题";
        }  else if ("complete".equals(stage)) {
            return "已完成";
        } else {
            throw new BusinessException("未知的答题阶段：" + stage);
        }
    }
}
