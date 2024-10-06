package top.ybgnb.bilibili.backup.bean;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName SessionPageData
 * @Description 私信
 * @Author hzhilong
 * @Time 2024/9/23
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class SessionPageData extends PageData<JSONObject> {

    @JSONField(name = "has_more")
    private Integer hasMore;

    private List<JSONObject> session_list;

    @Override
    public boolean hasMore(int currentTotal) {
        if (hasMore == 1) {
            return true;
        }
        return false;
    }

    @Override
    public List<JSONObject> _getList() {
        return this.session_list;
    }

    @Override
    public void _setList(List<JSONObject> list) {
        this.session_list = list;
    }
}
