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
}