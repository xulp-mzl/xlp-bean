package org.xlp.bean.util;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.exception.BeanBaseException;

/**
 * 根据类名获取相应Class对象工具类
 */
public class ClassForNameUtils {
    /**
     * 根据类名获取相应Class对象
     * @param className 类名称
     * @return Class对象
     * @throws BeanBaseException 假如根据类名获取不到相应的Class对象，则抛出该异常
     * @throws NullPointerException 假如参数为null或空，则抛出该异常
     */
    public static Class<?> forName(String className){
        AssertUtils.isNotNull(className, "className parameter is null or empty!");
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeanBaseException(e);
        }
    }
}
