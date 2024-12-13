package io.github.hzhilong.bilibili.backup.api.bean.list;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 列表数据
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class ListData<L> {
    List<L> list;
    int count;
}
