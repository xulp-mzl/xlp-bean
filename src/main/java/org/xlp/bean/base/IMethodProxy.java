package org.xlp.bean.base;

import java.lang.reflect.Method;

/**
 * 该接口是处理更加复杂场景下的方法执行时是否需要做增强处理
 * 该接口需要配合<code>{@link org.xlp.bean.annotation.MethodProxy}</code>注解使用
 * @see org.xlp.bean.annotation.MethodProxy
 */
@FunctionalInterface
public interface IMethodProxy {
    /**
     * 控制指定对象的方法是否需要被增强
     * @param object 对象
     * @param method 该对象的方法
     * @param params 该对象方法的参数
     * @return true: 该方法需要增强，否则不需要
     */
    boolean proxy(Object object, Method method, Object[] params);
}
