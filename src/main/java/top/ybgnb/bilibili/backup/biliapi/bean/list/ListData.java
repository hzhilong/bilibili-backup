package top.ybgnb.bilibili.backup.biliapi.bean.list;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ListData
 * @Description
 * @Author hzhilong
 * @Time 2024/9/20
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class ListData<L> {
    List<L> list;
    int count;
}
