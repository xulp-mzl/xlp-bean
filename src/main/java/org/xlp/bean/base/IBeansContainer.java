package org.xlp.bean.base;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.exception.BeanDefinitionExistException;
import org.xlp.bean.exception.BeanExistException;

/**
 * bean 容器接口，存放所有的bean定义信息
 */
public interface IBeansContainer extends IBeanFactory{
    /**
     * 向容器中添加bean定义对象
     * @param beanDefinition bean定义对象
     * @throws BeanDefinitionExistException 假如容器中存在，则抛出该异常
     */
     default void addBeanDefinition(IBeanDefinition beanDefinition) throws BeanDefinitionExistException{
         addBeanDefinition(beanDefinition, false);
     }

    /**
     * 向容器中添加bean定义对象
     * @param beanDefinition bean定义对象
     * @param covering 是否覆盖已有的bean定义，true；是  false: 否
     * @throws BeanDefinitionExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     */
    void addBeanDefinition(IBeanDefinition beanDefinition, boolean covering) throws BeanDefinitionExistException;

    /**
     * 向容器中添加指定类型的bean定义对象
     * @param beanClass bean定义对象
     * @throws BeanDefinitionExistException 假如容器中存在，则抛出该异常
     */
    default void addBeanDefinition(Class<?> beanClass) throws BeanDefinitionExistException{
        addBeanDefinition(beanClass, false);
    }

    /**
     * 向容器中添加指定类型的bean定义对象
     * @param beanClass bean定义对象
     * @param covering 是否覆盖已有的bean定义，true；是  false: 否
     * @throws BeanDefinitionExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     */
    void addBeanDefinition(Class<?> beanClass, boolean covering) throws BeanDefinitionExistException;

    /**
     * 判断bean定义是否在bean容器中已存在
     * @param beanDefinition
     * @return true：存在，false：不存在
     */
    default boolean hasBeanDefinition(IBeanDefinition beanDefinition){
        if (beanDefinition == null) return false;
        BeanDefinitionExistType existType = judgeBeanDefinition(beanDefinition);
        return existType == BeanDefinitionExistType.BY_BEAN_ID
                || existType == BeanDefinitionExistType.BY_BEAN_CLASS_NAME;
    }

    /**
     * 判断bean定义已在容器中存在的类型
     * @param beanDefinition
     * @return
     */
    BeanDefinitionExistType judgeBeanDefinition(IBeanDefinition beanDefinition);

    /**
     * 向容器中添加指定ID的bean对象
     * @param bean
     * @param beanId
     * @param covering 是否覆盖已有的bean定义，true；是  false: 否
     * @param types 对应的泛型信息
     * @param <T>
     * @throws BeanExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     */
    <T> void addBean(T bean, String beanId, boolean covering, Class<?>... types) throws BeanExistException;

    /**
     * 向容器中添加指定ID的bean对象
     * @param bean
     * @param beanId
     * @param covering 是否覆盖已有的bean定义，true；是  false: 否
     * @param <T>
     * @throws BeanExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     */
    default <T> void addBean(T bean, String beanId, boolean covering) throws BeanExistException {
        addBean(bean, beanId, covering, new Class<?>[0]);
    }

    /**
     * 向容器中添加指定ID的bean对象
     * @param bean
     * @param beanId
     * @param <T>
     * @throws BeanExistException 假如容器中存指定ID的bean，则抛出该异常
     */
    default <T> void addBean(T bean, String beanId) throws BeanExistException {
        addBean(bean, beanId, false);
    }

    /**
     * 向容器中添加指定类型的bean
     * @param bean
     * @param beanClass
     * @param covering 是否覆盖已有的bean定义，true；是  false: 否
     * @param types 对应的泛型信息
     * @param <T>
     * @throws BeanExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     */
    <T> void addBean(T bean, Class<? super T> beanClass, boolean covering, Class<?>... types) throws BeanExistException;

    /**
     * 向容器中添加指定类型的bean
     * @param bean
     * @param beanClass
     * @param covering 是否覆盖已有的bean定义，true；是  false: 否
     * @param <T>
     * @throws BeanExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     */
    default <T> void addBean(T bean, Class<? super T> beanClass, boolean covering) throws BeanExistException{
        addBean(bean, beanClass, covering, new Class<?>[0]);
    }

    /**
     * 向容器中添加指定类型的bean
     * @param bean
     * @param beanClass
     * @param types 对应的泛型信息
     * @param <T>
     * @throws BeanExistException 假如容器中存在，则抛出该异常
     */
    default <T> void addBean(T bean, Class<? super T> beanClass, Class<?>... types) throws BeanExistException{
        addBean(bean, beanClass, false, types);
    }

    /**
     * 向容器中添加指定类型的bean
     * @param bean
     * @param beanClass
     * @param <T>
     * @throws BeanExistException 假如容器中存在，则抛出该异常
     */
    default <T> void addBean(T bean, Class<? super T> beanClass) throws BeanExistException{
        addBean(bean, beanClass, false);
    }

    /**
     * 向容器中添加指定类型的bean
     * @param bean
     * @param <T>
     * @throws BeanExistException 假如容器中存，则抛出该异常
     */
    default <T> void addBean(T bean) throws BeanExistException {
        addBean(bean, false);
    }


    /**
     * 向容器中添加指定类型的bean
     * @param bean
     * @param covering 是否覆盖已有的bean定义，true；是  false: 否
     * @param types 对应的泛型信息
     * @param <T>
     * @throws BeanExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     * @throws NullPointerException 假如参数为null，则抛出该异常
     */
    @SuppressWarnings("unchecked")
    default <T> void addBean(T bean, boolean covering, Class<?>... types) throws BeanExistException{
        AssertUtils.isNotNull(bean, "bean parameter is null!");
        addBean(bean, (Class<T>)bean.getClass(), covering, types);
    }

    /**
     * 向容器中添加指定类型的bean
     * @param bean
     * @param covering 是否覆盖已有的bean定义，true；是  false: 否
     * @param <T>
     * @throws BeanExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     * @throws NullPointerException 假如参数为null，则抛出该异常
     */
    default <T> void addBean(T bean, boolean covering) throws BeanExistException {
        addBean(bean, covering, new Class<?>[0]);
    }

    /**
     * 向容器中添加指定类型的bean
     * @param bean
     * @param types 对应的泛型信息
     * @param <T>
     * @throws BeanExistException 假如容器中存，则抛出该异常
     */
    default <T> void addBean(T bean, Class<?>... types) throws BeanExistException{
        addBean(bean, false, types);
    }

    /**
     * 重置容器中的数据
     */
    void reset();

    /**
     * bean定义存在类型
     */
    enum BeanDefinitionExistType{
        /**
         * 存在beanID相同的bean定义
         */
        BY_BEAN_ID,

        /**
         * 存在bean类型相同的bean定义
         */
        BY_BEAN_CLASS_NAME,

        /**
         * 不存在
         */
        NONE
    }
}