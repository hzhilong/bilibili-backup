package top.ybgnb.bilibili.backup.app.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;

/**
 * @ClassName SavedUser
 * @Description 保存的用户
 * @Author hzhilong
 * @Time 2024/11/22
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedUser extends Upper {

    private String cookie;

    public SavedUser(Upper upper, String cookie) {
        super(upper.getMid(), upper.getName(), upper.getFace());
        this.cookie = cookie;
    }

}
