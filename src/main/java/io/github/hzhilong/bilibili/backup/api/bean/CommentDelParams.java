package io.github.hzhilong.bilibili.backup.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hzhilong
 * @version 1.0
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class CommentDelParams {

    private String oid;
    private String rpid;
    private String type;

    public String toCacheKey() {
        return this.getType() + "-" + this.getOid() + "-" + this.getRpid();
    }
}
