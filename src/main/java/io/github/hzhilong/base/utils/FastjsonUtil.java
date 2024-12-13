package io.github.hzhilong.base.utils;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * fastjson工具
 *
 * @author hzhilong
 * @version 1.0
 */
public class FastjsonUtil {

    /**
     * 获取泛型信息
     *
     * @param type      带有父类的泛型信息，this.getClass().getGenericSuperclass();
     * @param currClass 当前实例的类，this.getClass()
     * @return 可供框架解析json的泛型信息
     */
    public static Type getParameterizedTypeImpl(Type type, Class<?> currClass) {
        if (!(type instanceof ParameterizedType)) {
            return type;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Type[] newTypeArguments = new Type[actualTypeArguments.length];
        for (int i = 0; i < actualTypeArguments.length; i++) {
            newTypeArguments[i] = getParameterizedTypeImpl(actualTypeArguments[i], null);
        }
        return new ParameterizedTypeImpl(newTypeArguments, null, currClass != null ? currClass : ((ParameterizedType) type).getRawType());
    }


}
