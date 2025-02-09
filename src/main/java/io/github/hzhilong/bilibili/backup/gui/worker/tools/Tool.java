package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工具
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tool {

    private String name;
    private String desc;
    private RunnableBuilder runnableBuilder;
    private boolean isTip = true;

    public Tool(String name, String desc, RunnableBuilder runnableBuilder) {
        this.name = name;
        this.desc = desc;
        this.runnableBuilder = runnableBuilder;
    }
}
