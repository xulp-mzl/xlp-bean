package org.xlp.bean.base;

import java.lang.reflect.Type;

/**
 * 需要获取泛型信息接口
 */
public interface IParameterizedType {
    /**
     * 获取泛型信息
     * @return
     */
    Type[] getActualType();

    /**
     * 设置泛型信息
     * @param types
     */
    void setActualType(Type[] types);
}
