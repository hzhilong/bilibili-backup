package io.github.hzhilong.bilibili.backup.api.bean.page;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 滑动 分页数据
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class CursorPageTotal {

    @NoArgsConstructor
    @Data
    public static class TotalCursor {
        private long id;
        @JSONField(name = "is_end")
        private boolean isEnd;
        private long time;
    }

    private TotalCursor cursor;

    private List<JSONObject> items;


}
