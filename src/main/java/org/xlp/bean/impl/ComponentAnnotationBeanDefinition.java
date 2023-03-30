package org.xlp.bean.impl;

import org.xlp.bean.annotation.Component;
import org.xlp.bean.base.AbstractBeanDefinition;
import org.xlp.bean.base.IBeanFields;
import org.xlp.bean.exception.BeanBaseException;

/**
 * <code>{@link org.xlp.bean.annotation.Component}</code>标记的类定义信息
 * @see org.xlp.bean.annotation.Component
 */
public class ComponentAnnotationBeanDefinition extends AbstractBeanDefinition {
    /**
     * Component注解
     */
    private Component component;

    /**
     * 构造函数
     *
     * @param beanFields 获取IBeanField数组信息的对象
     * @throws NullPointerException 假如参数为null，则抛出该异常
     * @throws IllegalArgumentException 假如不是{@link Component}注解标记的类对应的{@link IBeanFields}，则抛出该异常
     */
    public ComponentAnnotationBeanDefinition(IBeanFields beanFields) {
        super(beanFields);
        Class<?> beanClass = beanFields.getBeanClass();
        setComponentAnnotation(beanClass);
    }

    /**
     * 构造函数
     *
     * @param beanClass bean类型
     * @throws NullPointerException 假如参数为null 则抛出该异常
     * @throws IllegalArgumentException 假如给定的类没有{@link Component}注解标记，则抛出该异常
     */
    public ComponentAnnotationBeanDefinition(Class<?> beanClass) {
        super(beanClass);
        setComponentAnnotation(beanClass);
    }

    /**
     * 构造函数
     *
     * @param beanClassName bean类型全路径名称
     * @throws NullPointerException 假如参数为null或空 则抛出该异常
     * @throws BeanBaseException    假如获取beanClassName对应的Class对象出错，则抛出该异常
     * @throws IllegalArgumentException 假如给定的名称对应的类没有{@link Component}注解标记，则抛出该异常
     */
    public ComponentAnnotationBeanDefinition(String beanClassName) {
        super(beanClassName);
        setComponentAnnotation(beanClass);
    }

    /**
     * 设置Component注解
     * @param beanClass bean 类型
     * @throws IllegalArgumentException 假如给定的类没有{@link Component}注解标记，则抛出该异常
     */
    private void setComponentAnnotation(Class<?> beanClass) {
        this.component = beanClass.getDeclaredAnnotation(Component.class);
        if (component == null){
            throw new IllegalArgumentException("给的的类[" + beanClass.getName() + "]没有[org.xlp.bean.annotation.Component]注解标记");
        }
    }

    /**
     * 是否需要被代理
     *
     * @return true: 是， false: 否
     * @see Component#proxy()
     */
    @Override
    public boolean isProxy() {
        return component.proxy();
    }

    /**
     * 是否是单例
     *
     * @return true: 是， false：否
     * @see Component#singleton()
     */
    @Override
    public boolean isSingleton() {
        return component.singleton();
    }

    /**
     * 是否是延迟实例化对象
     *
     * @return true: 是， false：否
     * @see Component#lazy()
     */
    @Override
    public boolean isLazy() {
        return component.lazy();
    }

    /**
     * 获取beanId
     *
     * @return bean id, 为配置返回 null
     * @see Component#id()
     */
    @Override
    public String getBeanId() {
        return component.id();
    }

    /**
     * 获取bean的描述
     *
     * @return bean描述
     * @see Component#description()
     */
    @Override
    public String getDescription() {
        return component.description();
    }
}
