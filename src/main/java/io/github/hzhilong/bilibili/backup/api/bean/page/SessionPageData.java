package io.github.hzhilong.bilibili.backup.api.bean.page;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 私信 分页数据
 *
 * @author hzhilong
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class SessionPageData extends PageData<JSONObject> {

    @JSONField(name = "has_more")
    private Integer hasMore2;

    @JSONField(name = "session_list")
    private List<JSONObject> sessionList;

    @Override
    public boolean hasMore(int currentTotal) {
        return hasMore2 == 1;
    }

    @Override
    public List<JSONObject> getList() {
        return this.sessionList;
    }

    @Override
    public void setList(List<JSONObject> list) {
        this.sessionList = list;
    }
}
