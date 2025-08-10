package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 答题提交结果
 *
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
public class QuestionCheckResult {
    private boolean passed;
    private boolean is_right;
    private String ans_right;
}
