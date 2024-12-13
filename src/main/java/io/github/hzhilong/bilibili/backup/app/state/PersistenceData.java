package io.github.hzhilong.bilibili.backup.app.state;

/**
 * 可持久化的数据
 *
 * @author hzhilong
 * @version 1.0
 */
public interface PersistenceData {

    String read(String key);

    Object write(String key, String value);

    Object delete(String key);

    void persistent();

}
