package org.xlp.bean.base;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.annotation.Component;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * bean定义抽象类
 * Create by xlp on 2023/3/25
 */
public abstract class AbstractBeanDefinition implements IBeanDefinition{
    /**
     * bean的类型
     */
    private Class<?> beanClass;

    /**
     * 是否需要被代理
     */
    private boolean proxy;

    /**
     * 是否延迟实例化
     */
    private boolean lazy;

    /**
     * 是否是单例
     */
    private boolean singleton;

    /**
     * bean id
     */
    private String beanId;

    /**
     * bean 描述信息
     */
    private String description;

    /**
     * 是否需要被代理
     *
     * @return true: 是， false: 否
     * @see Component#proxy()
     */
    @Override
    public boolean isProxy() {
        return proxy;
    }

    /**
     * 设置bean是否被代理
     *
     * @param proxy
     */
    @Override
    public void setProxy(boolean proxy) {
        this.proxy = proxy;
    }

    /**
     * 是否是单例
     *
     * @return true: 是， false：否
     * @see Component#singleton()
     */
    @Override
    public boolean isSingleton() {
        return singleton;
    }

    /**
     * 设置是否单例
     *
     * @param singleton
     */
    @Override
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * 是否是延迟实例化对象
     *
     * @return true: 是， false：否
     * @see Component#lazy()
     */
    @Override
    public boolean isLazy() {
        return lazy;
    }

    /**
     * 设置是否延迟实例化对象
     *
     * @param lazy
     */
    @Override
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
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
     * 设置beanId
     *
     * @param beanId
     */
    @Override
    public void setBeanId(String beanId) {
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
        return description;
    }

    /**
     * 设置描述信息
     *
     * @param description
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 是否是接口
     *
     * @return true：是， false：否
     */
    @Override
    public boolean isInterface() {
        assertBeanClass();
        return beanClass.isInterface();
    }

    private void assertBeanClass() {
        AssertUtils.isNotNull(beanClass, "bean 类型为null！");
    }

    /**
     * 是否是抽象类
     *
     * @return true：是， false：否
     */
    @Override
    public boolean isAbstract() {
        assertBeanClass();
        return !isInterface() && Modifier.isAbstract(beanClass.getModifiers());
    }

    /**
     * 获取父类信息
     *
     * @return 超类信息
     */
    @Override
    public Class<?> getSupperClass() {
        assertBeanClass();
        return beanClass.getSuperclass();
    }

    /**
     * 获取bean class
     *
     * @return bean class对象
     */
    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * 获取
     *
     * @return
     */
    @Override
    public IBeanField[] getBeanFields() {
        return new IBeanField[0];
    }

    /**
     * 获取父类泛型信息，假如无泛型类型，则返回空数组
     *
     * @return
     */
    @Override
    public Type[] getActualType() {
        assertBeanClass();
        // 获取父类的泛型类型
        Type type = beanClass.getGenericSuperclass();
        if (type instanceof ParameterizedType){
            return ((ParameterizedType)type).getActualTypeArguments();
        }
        return new Type[0];
    }
}
