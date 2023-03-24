package org.xlp.bean.base;

import org.xlp.bean.exception.BeanBaseException;
import org.xlp.bean.exception.BeanExistException;
import org.xlp.bean.exception.NotSuchBeanException;

/**
 * bean 容器接口，存放所有的bean定义信息
 */
public interface IBeansContainer {
    /**
     * 向容器中添加bean定义对象
     * @param beanDefinition bean定义对象
     * @throws BeanExistException 加入容器中存在，则抛出该异常
     */
     void addBeanDefinition(IBeanDefinition beanDefinition) throws BeanBaseException;

    /**
     * 向容器中添加指定类型的bean定义对象
     * @param beanClass bean定义对象
     * @throws BeanExistException 加入容器中存在，则抛出该异常
     */
    void addBeanDefinition(Class<?> beanClass) throws BeanBaseException;

    /**
     * 向容器中添加bean实例对象
     * @param bean
     * @throws BeanExistException 加入容器中存在，则抛出该异常
     */
    <T> void addBean(T bean) throws BeanExistException;

    /**
     * 向容器中添加指定id的bean对象
     * @param beanId bean id
     * @param bean bean 对象
     * @throws BeanExistException
     */
    <T> void addBean(String beanId, T bean) throws BeanExistException;

    /**
     * 从容器中相应id的bean对象
     * @param beanId beanId
     * @return
     * @throws NotSuchBeanException
     */
    <T> T getBean(String beanId) throws NotSuchBeanException;

    /**
     * 从容器中相应类型的bean对象
     * @param beanClass
     * @return
     * @throws NotSuchBeanException
     */
    <T, I> T getBean(Class<I> beanClass) throws NotSuchBeanException;
}