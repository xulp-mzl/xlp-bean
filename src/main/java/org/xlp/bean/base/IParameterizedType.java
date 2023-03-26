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
}
