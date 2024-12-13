package io.github.hzhilong.bilibili.backup.api.user;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import io.github.hzhilong.bilibili.backup.app.constant.AppConstant;

/**
 * 账号信息
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
public class User {
    private String uid;
    private String cookie;

    @JSONField(name="bili_jct")
    private String biliJct;
    /**
     * 是否已注销
     */
    private boolean isCancelledAccount;

    public User(String cookieOrUid) {
        if (AppConstant.NUM_PATTERN.matcher(cookieOrUid).find()) {
            this.uid = cookieOrUid;
            this.isCancelledAccount = true;
        } else {
            int tempIndex = cookieOrUid.indexOf("DedeUserID=");
            this.uid = cookieOrUid.substring(tempIndex + 11, tempIndex + 11 + cookieOrUid.substring(tempIndex + 11).indexOf(";"));
            this.cookie = cookieOrUid;
            tempIndex = cookieOrUid.lastIndexOf("bili_jct=");
            this.biliJct = cookieOrUid.substring(tempIndex + 9, tempIndex + 41);
            this.isCancelledAccount = false;
        }
    }


}
