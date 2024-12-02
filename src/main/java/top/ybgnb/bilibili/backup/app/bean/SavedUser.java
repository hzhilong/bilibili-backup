package top.ybgnb.bilibili.backup.app.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.ybgnb.bilibili.backup.biliapi.bean.Upper;

import java.util.Objects;

/**
 * @ClassName SavedUser
 * @Description 保存的账号
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

    @Override
    public String toString() {
        if (this.getName() != null && this.getName().length() > 8) {
            return String.format("%s...(%s)", this.getName().substring(0, 8), this.getMid());
        }
        return String.format("%s(%s)", this.getName(), this.getMid());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SavedUser)) {
            return false;
        }

        SavedUser user = (SavedUser) obj;
        return Objects.equals(user.getMid(), this.getMid());
    }
}
