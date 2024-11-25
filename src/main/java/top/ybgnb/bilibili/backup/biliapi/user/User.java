package top.ybgnb.bilibili.backup.biliapi.user;

import lombok.Data;
import top.ybgnb.bilibili.backup.app.constant.AppConstant;

/**
 * @ClassName User
 * @Description
 * @Author hzhilong
 * @Time 2024/9/22
 * @Version 1.0
 */
@Data
public class User {
    private String uid;
    private String cookie;
    private String bili_jct;
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
            this.bili_jct = cookieOrUid.substring(tempIndex + 9, tempIndex + 41);
            this.isCancelledAccount = false;
        }
    }


}
