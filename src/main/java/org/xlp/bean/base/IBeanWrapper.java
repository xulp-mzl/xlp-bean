package org.xlp.bean.base;

import java.lang.reflect.Method;

/**
 * bean 包装器接口，实现该接口的类并且有<code>{@link org.xlp.bean.annotation.Component}</code>标记
 * 该类的实例会通过代理创建
 */
public interface IBeanWrapper {
    /**
     * 代理对象函数执行前处理函数名称
     * <code>{@link #beforeExecute(Method, Object[])}</code>
     */
    static final String BEFORE_EXECUTE_METHOD_NAME = "beforeExecute";

    /**
     * 代理对象函数执行后处理函数名称
     * <code>{@link #afterExecute(Method, Object[])}</code>
     */
    static final String AFTER_EXECUTE_METHOD_NAME = "afterExecute";

    /**
     * 代理对象函数执行抛出异常后处理函数名称
     * <code>{@link #throwExecute(Method, Object[], Throwable)}</code>
     */
    static final String THROW_EXECUTE_METHOD_NAME = "throwExecute";
    
    /**
     * 代理对象函数执行前需要执行的操作
     * @param method 被代理对象的方法对象
     * @param params 被代理对象方法的参数
     */
    default void beforeExecute(Method method, Object[] params) {}

    /**
     * 代理对象函数执行后需要执行的操作
     * @param method 被代理对象的方法对象
     * @param params 被代理对象方法的参数
     */
    default void afterExecute(Method method, Object[] params) {}

    /**
     * 代理对象函数执行抛出异常是需要执行的操作
     * @param method 被代理对象的方法对象
     * @param params 被代理对象方法的参数
     * @param throwable 被代理对象方法抛出的异常
     */
    default void throwExecute(Method method, Object[] params, Throwable throwable) {}
}
