package org.xlp.bean.impl;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.annotation.Component;
import org.xlp.bean.base.IBeanDefinition;
import org.xlp.bean.base.IBeansContainer;
import org.xlp.bean.exception.BeanBaseException;
import org.xlp.bean.exception.BeanDefinitionExistException;
import org.xlp.bean.exception.NotSuchBeanException;
import org.xlp.utils.XLPStringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean 容器的默认实现
 */
public class DefaultBeansContainer implements IBeansContainer {
    /**
     * 存储bean id 与 {@link IBeanDefinition} 映射集合
     * <p>key: beanId, value: {@link IBeanDefinition}对象</p>
     */
    private final Map<String, IBeanDefinition> beanIdBeanDefinitionMap = new ConcurrentHashMap<>(8);

    /**
     * 存储bean类名 与 {@link IBeanDefinition} 映射集合
     * <p>key: beanClassName , value: {@link IBeanDefinition}对象</p>
     */
    private final Map<String, IBeanDefinition> beanClassNameBeanDefinitionMap = new ConcurrentHashMap<>(8);

    /**
     * 存储bean id 与 bean对象 映射集合
     * <p>key: beanId, value: bean对象</p>
     */
    private final Map<String, Object> beanMap = new ConcurrentHashMap<>(8);

    /**
     * 存储bean类名 与 bean对象 映射集合
     * <p>key: beanClassName , value: bean对象</p>
     */
    private final Map<String, Object> beanClassNameBeanMap = new ConcurrentHashMap<>(8);

    /**
     * 向容器中添加bean定义对象
     *
     * @param beanDefinition bean定义对象
     * @throws BeanDefinitionExistException 假如容器中存在，则抛出该异常
     * @throws NullPointerException 假如参数为空则抛出该异常
     */
    @Override
    public void addBeanDefinition(IBeanDefinition beanDefinition) throws BeanDefinitionExistException {
        AssertUtils.isNotNull(beanDefinition, "beanDefinition parameter is null!");
        String beanId = beanDefinition.getBeanId();
        String className = XLPStringUtil.emptyTrim(beanDefinition.getBeanClassName());
        synchronized (this){
            // 判读是否已经存在相应的bean定义信息，如果存在，则抛出相应的异常
            if(!XLPStringUtil.isEmpty(beanId)){
                IBeanDefinition beanDefinition1 = beanIdBeanDefinitionMap.get(beanId);
                if (beanDefinition1 != null){
                    throw new BeanDefinitionExistException(beanId);
                }
                beanIdBeanDefinitionMap.put(beanId, beanDefinition);
            } else {
                IBeanDefinition beanDefinition1 = beanClassNameBeanDefinitionMap.get(className);
                if (beanDefinition1 != null){
                    throw new BeanDefinitionExistException(className);
                }
                beanClassNameBeanDefinitionMap.put(className, beanDefinition);
            }
        }
    }

    /**
     * 预先创建所有非延迟加载的单例bean
     * @see Component#lazy()
     */
    public void createBeans(){

    }

    /**
     * 更具bean定义创建bean对象
     * @param beanDefinition
     */
    private void createBean(IBeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton() && !beanDefinition.isLazy()
                && !beanDefinition.isInterface() && !beanDefinition.isAbstract()){
            // 不可实例化的对象之间返回
            return;
        }

    }

    /**
     * 向容器中添加指定类型的bean定义对象
     * <p>注意：使用该方法时，传的Class对象必须要有{@link org.xlp.bean.annotation.Component}注解标记，否则会抛出异常</p>
     *
     * @param beanClass bean定义对象
     * @throws BeanDefinitionExistException 假如容器中存在，则抛出该异常
     * @throws NullPointerException 假如参数为空则抛出该异常
     * @see ComponentAnnotationBeanDefinition
     */
    @Override
    public void addBeanDefinition(Class<?> beanClass) throws BeanDefinitionExistException {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        this.addBeanDefinition(new ComponentAnnotationBeanDefinition(beanClass));
    }

    /**
     * 统一抛出异常
     * @param key
     * @param keyIsId
     * @return
     * @throws NotSuchBeanException 假如未找到指定条件bean则抛出该异常
     */
    private IBeanDefinition getBeanDefinitionAndThrowNotSuchBeanException(String key, boolean keyIsId) throws NotSuchBeanException{
        IBeanDefinition beanDefinition;
        if (keyIsId){
            beanDefinition = beanIdBeanDefinitionMap.get(key);
        } else {
            beanDefinition = beanClassNameBeanDefinitionMap.get(key);
        }
        if (beanDefinition == null){
            throw new NotSuchBeanException(key);
        }
        return beanDefinition;
    }

    /**
     * 判断给的id的bean是否被代理
     *
     * @param id bean id
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     */
    @Override
    public boolean isProxy(String id) throws NotSuchBeanException {
        return getBeanDefinitionAndThrowNotSuchBeanException(id, true).isProxy();
    }

    /**
     * 判断给的类全路径名称的bean是否被代理
     *
     * @param className 类全路径名称
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定类名的bean则抛出该异常
     */
    @Override
    public boolean isProxyByClassName(String className) throws NotSuchBeanException {
        return getBeanDefinitionAndThrowNotSuchBeanException(className, false).isProxy();
    }

    /**
     * 判断给的id的bean是否是单例
     *
     * @param id bean id
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     */
    @Override
    public boolean isSingleton(String id) throws NotSuchBeanException {
        return getBeanDefinitionAndThrowNotSuchBeanException(id, true).isSingleton();
    }

    /**
     * 判断给的类全路径名称的bean是否是单例
     *
     * @param className 类全路径名称
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定类名的bean则抛出该异常
     */
    @Override
    public boolean isSingletonByClassName(String className) throws NotSuchBeanException {
        return getBeanDefinitionAndThrowNotSuchBeanException(className, false).isSingleton();
    }

    /**
     * 判断给的id的bean是否延迟实例化
     *
     * @param id bean id
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     */
    @Override
    public boolean isLazy(String id) throws NotSuchBeanException {
        return getBeanDefinitionAndThrowNotSuchBeanException(id, true).isLazy();
    }

    /**
     * 判断给的类全路径名称的bean是否延迟实例化
     *
     * @param className 类全路径名称
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定类名的bean则抛出该异常
     */
    @Override
    public boolean isLazyByClassName(String className) throws NotSuchBeanException {
        return getBeanDefinitionAndThrowNotSuchBeanException(className, false).isLazy();
    }

    /**
     * 从容器中相应id的bean对象
     *
     * @param beanId beanId
     * @return
     * @throws NotSuchBeanException
     */
    @Override
    public <T> T getBean(String beanId) throws NotSuchBeanException {
        return null;
    }

    /**
     * 从容器中相应类型的bean对象
     *
     * @param beanClass
     * @return
     * @throws NotSuchBeanException
     */
    @Override
    public <T, I> T getBean(Class<I> beanClass) throws NotSuchBeanException {
        return null;
    }

    /**
     * 获取该类全路径名称的bean对象
     *
     * @param className 类全路径名称
     * @return bean 对象
     * @throws BeanBaseException 假如或bean过程失败，则抛出该异常
     */
    @Override
    public <T> T getBeanByClassName(String className) throws BeanBaseException {
        return null;
    }

    /**
     * 获取给的类的所有对应的beanId数据
     *
     * @param clazz 要操作的类
     * @return Id数据
     */
    @Override
    public <T> String[] getAlias(Class<T> clazz) {
        List<String> beanIds = new ArrayList<>();
        beanIdBeanDefinitionMap.forEach((key, value) -> {
            if (value.getBeanClass() == clazz){
                beanIds.add(key);
            }
        });
        return beanIds.toArray(new String[0]);
    }
}
