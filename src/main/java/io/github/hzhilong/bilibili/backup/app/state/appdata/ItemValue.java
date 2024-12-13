package io.github.hzhilong.bilibili.backup.app.state.appdata;

import com.alibaba.fastjson.JSON;
import io.github.hzhilong.base.utils.FastjsonUtil;
import io.github.hzhilong.base.utils.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 应用数据项的值
 *
 * @author hzhilong
 * @version 1.0
 */
public abstract class ItemValue<D> {

    private D value;

    public ItemValue() {
        this.value = null;
    }

    public ItemValue(D value) {
        this.value = value;
    }

    /**
     * 默认值
     *
     * @return
     */
    public D defaultValue() {
        return value;
    }

    /**
     * 解析数据
     *
     * @param persistenceData 序列化的字符串
     * @return 数据
     */
    D parseData(String persistenceData) {
        if (StringUtils.isEmpty(persistenceData)) {
            return null;
        }
        Type type = this.getClass().getGenericSuperclass();
        Type parameterizedTypeImpl = FastjsonUtil.getParameterizedTypeImpl(type, this.getClass());
        ParameterizedType parameterizedType = (ParameterizedType) parameterizedTypeImpl;
        return JSON.parseObject(persistenceData, parameterizedType.getActualTypeArguments()[0]);
    }

    /**
     * 序列化数据
     *
     * @param data 数据
     * @return 序列化的字符串
     */
    String persistenceData(D data) {
        return JSON.toJSONString(data);
    }

}
