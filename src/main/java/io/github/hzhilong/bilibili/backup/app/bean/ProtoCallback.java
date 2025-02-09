package io.github.hzhilong.bilibili.backup.app.bean;

import java.io.InputStream;

/**
 * @author hzhilong
 * @version 1.0
 */
public interface ProtoCallback<D> {
    D parse(InputStream input);
}
