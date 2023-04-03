package org.xlp.bean.base;

import org.xlp.bean.exception.BeanDefinitionExistException;

/**
 * bean 容器接口，存放所有的bean定义信息
 */
public interface IBeansContainer extends IBeanFactory{
    /**
     * 向容器中添加bean定义对象
     * @param beanDefinition bean定义对象
     * @throws BeanDefinitionExistException 加入容器中存在，则抛出该异常
     */
     void addBeanDefinition(IBeanDefinition beanDefinition) throws BeanDefinitionExistException;

    /**
     * 向容器中添加指定类型的bean定义对象
     * @param beanClass bean定义对象
     * @throws BeanDefinitionExistException 加入容器中存在，则抛出该异常
     */
    void addBeanDefinition(Class<?> beanClass) throws BeanDefinitionExistException;

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