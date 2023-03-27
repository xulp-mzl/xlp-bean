package org.xlp.bean.base;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.exception.BeanBaseException;

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
    protected Class<?> beanClass;

    /**
     * bean的类型全路径名
     */
    private final String beanClassName;

    /**
     * 构造函数
     * @param beanClass bean类型
     * @throws NullPointerException 假如参数为null 则抛出该异常
     */
    public AbstractBeanDefinition(Class<?> beanClass){
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        this.beanClass = beanClass;
        this.beanClassName = beanClass.getName();
    }

    /**
     * 构造函数
     * @param beanClassName bean类型全路径名称
     * @throws NullPointerException 假如参数为null或空 则抛出该异常
     * @throws BeanBaseException 假如获取beanClassName对应的Class对象出错，则抛出该异常
     */
    public AbstractBeanDefinition(String beanClassName){
        AssertUtils.isNotNull(beanClassName, "beanClassName parameter is null or empty!");
        try {
            this.beanClass = Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new BeanBaseException(e);
        }
        this.beanClassName = beanClassName;
    }

    /**
     * 是否是接口
     *
     * @return true：是， false：否
     */
    @Override
    public boolean isInterface() {
        return beanClass.isInterface();
    }

    /**
     * 是否是抽象类
     *
     * @return true：是， false：否
     */
    @Override
    public boolean isAbstract() {
        return !isInterface() && Modifier.isAbstract(beanClass.getModifiers());
    }

    /**
     * 获取父类信息
     *
     * @return 超类信息
     */
    @Override
    public Class<?> getSupperClass() {
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
        // 获取父类的泛型类型
        Type type = beanClass.getGenericSuperclass();
        if (type instanceof ParameterizedType){
            return ((ParameterizedType)type).getActualTypeArguments();
        }
        return new Type[0];
    }

    /**
     * 获取bean类型全路径名称
     *
     * @return bean类型全路径名称
     */
    @Override
    public String getBeanClassName() {
        return beanClassName;
    }
}
