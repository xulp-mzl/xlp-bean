package org.xlp.bean.impl;

import org.xlp.bean.annotation.Component;
import org.xlp.bean.base.AbstractBeanDefinition;
import org.xlp.bean.exception.BeanBaseException;

/**
 * 自定义注册Bean定义实现类，用于处理自定义的bean定义对象
 */
public class CustomRegisteredBeanDefinition extends AbstractBeanDefinition {
    /**
     * beanID
     */
    private String beanId;

    /**
     * 构造函数
     *
     * @param beanClass bean类型
     * @throws NullPointerException 假如参数为null 则抛出该异常
     */
    public CustomRegisteredBeanDefinition(Class<?> beanClass) {
        super(beanClass);
    }

    /**
     * 构造函数
     *
     * @param beanClassName bean类型全路径名称
     * @throws NullPointerException 假如参数为null或空 则抛出该异常
     * @throws BeanBaseException    假如获取beanClassName对应的Class对象出错，则抛出该异常
     */
    public CustomRegisteredBeanDefinition(String beanClassName) {
        super(beanClassName);
    }

    /**
     * 是否需要被代理
     *
     * @return true: 是， false: 否
     * @see Component#proxy()
     */
    @Override
    public boolean isProxy() {
        return false;
    }

    /**
     * 是否是单例
     *
     * @return true: 是， false：否
     * @see Component#singleton()
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 是否是延迟实例化对象
     *
     * @return true: 是， false：否
     * @see Component#lazy()
     */
    @Override
    public boolean isLazy() {
        return false;
    }

    /**
     * 获取beanId
     *
     * @return bean id, 为配置返回 null
     * @see Component#id()
     */
    @Override
    public String getBeanId() {
        return beanId;
    }

    /**
     * 设置beanID
     * @param beanId
     */
    public void setBeanId(String beanId){
        this.beanId = beanId;
    }

    /**
     * 获取bean的描述
     *
     * @return bean描述
     * @see Component#description()
     */
    @Override
    public String getDescription() {
        return null;
    }
}
