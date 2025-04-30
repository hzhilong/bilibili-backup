package io.github.hzhilong.bilibili.backup.gui.worker.tools;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工具
 *
 * @author hzhilong
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class Tool {

    private String name;
    private String desc;
    private RunnableBuilder runnableBuilder;
    private boolean isTip = true;
    private String tipMsg;

    public Tool(String name, String desc, RunnableBuilder runnableBuilder, boolean isTip, String tipMsg) {
        this.name = name;
        this.desc = desc;
        this.runnableBuilder = runnableBuilder;
        this.isTip = isTip;
        this.tipMsg = tipMsg;
    }

    public Tool(String name, String desc, RunnableBuilder runnableBuilder, boolean isTip) {
        this.name = name;
        this.desc = desc;
        this.runnableBuilder = runnableBuilder;
        this.isTip = isTip;
    }

    public Tool(String name, String desc, RunnableBuilder runnableBuilder) {
        this.name = name;
        this.desc = desc;
        this.runnableBuilder = runnableBuilder;
    }
}
