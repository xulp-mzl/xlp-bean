package org.xlp.bean.base;

import org.xlp.bean.annotation.Component;

/**
 * bean 定义信息，包含bean的所有基本信息
 */
public interface IBeanDefinition extends IParameterizedType{
    /**
     * 是否需要被代理
     * @see Component#proxy()
     * @return true: 是， false: 否
     */
    boolean isProxy();

    /**
     * 是否是单例
     * @see Component#singleton()
     * @return true: 是， false：否
     */
    boolean isSingleton();

    /**
     * 是否是延迟实例化对象
     * @see Component#lazy()
     * @return true: 是， false：否
     */
    boolean isLazy();

    /**
     * 获取beanId
     * @see Component#id()
     * @return bean id, 为配置返回 null
     */
    String getBeanId();

    /**
     * 获取bean的描述
     * @see Component#description()
     * @return bean描述
     */
    String getDescription();

    /**
     * 是否是接口
     * @return true：是， false：否
     */
    boolean isInterface();

    /**
     * 是否是抽象类
     * @return true：是， false：否
     */
    boolean isAbstract();

    /**
     * 获取父类信息
     * @return 超类信息
     */
    Class<?> getSupperClass();

    /**
     * 获取bean class
     * @return bean class对象
     */
    Class<?> getBeanClass();

    /**
     * 获取bean类型全路径名称
     * @return bean类型全路径名称
     */
    default String getClassName(){
        Class<?> beanClass = getBeanClass();
        return beanClass == null ? null : beanClass.getName();
    }

    /**
     * 获取
     * @return
     */
    IBeanField[] getBeanFields();
}
