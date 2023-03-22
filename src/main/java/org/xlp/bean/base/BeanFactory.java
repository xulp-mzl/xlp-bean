package org.xlp.bean.base;

import org.xlp.bean.exception.BeanBaseException;
import org.xlp.bean.exception.NotSuchBeanException;

/**
 * bean 工厂接口，主要用来实现bean的创建
 */
public interface BeanFactory {
    /**
     * 判断给的id的bean是否被代理
     *
     * @param id bean id
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     */
    boolean isProxy(String id) throws NotSuchBeanException;

    /**
     * 判断给的类全路径名称的bean是否被代理
     * @param className 类全路径名称
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定类名的bean则抛出该异常
     */
    boolean isProxyByClassName(String className) throws NotSuchBeanException;

    /**
     * 判断给的id的bean是否是单例
     * @param id bean id
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     */
    boolean isSingleton(String id) throws NotSuchBeanException;

    /**
     * 判断给的类全路径名称的bean是否是单例
     * @param className 类全路径名称
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定类名的bean则抛出该异常
     */
    boolean isSingletonByClassName(String className) throws NotSuchBeanException;

    /**
     * 获取该id的bean对象
     * @param id bean id
     * @return bean 对象
     * @throws BeanBaseException 假如或bean过程失败，则抛出该异常
     */
    <T> T getBean(String id) throws BeanBaseException;

    /**
     * 获取该类全路径名称的bean对象
     * @param className 类全路径名称
     * @return bean 对象
     * @throws BeanBaseException 假如或bean过程失败，则抛出该异常
     */
    <T> T getBeanByClassName(String className) throws BeanBaseException;

    /**
     * 获取给的类的所有Id数据
     * @param clazz 要操作的类
     * @return Id数据
     */
    <T> String[] getAlias(Class<T> clazz);
}
