package org.xlp.bean.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlp.assertion.AssertUtils;
import org.xlp.bean.annotation.Component;
import org.xlp.bean.base.IBeanCreator;
import org.xlp.bean.base.IBeanDefinition;
import org.xlp.bean.base.IBeanField;
import org.xlp.bean.base.IBeansContainer;
import org.xlp.bean.exception.*;
import org.xlp.bean.object.BeanObject;
import org.xlp.bean.util.ClassForNameUtils;
import org.xlp.utils.XLPArrayUtil;
import org.xlp.utils.XLPStringUtil;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean 容器的默认实现
 */
public class DefaultBeansContainer implements IBeansContainer {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultBeansContainer.class);

    /**
     * 存储bean id 与 {@link IBeanDefinition} 映射集合
     * <p>key: beanId, value: {@link IBeanDefinition}对象</p>
     */
    protected final Map<String, IBeanDefinition> beanIdBeanDefinitionMap = new ConcurrentHashMap<>(8);

    /**
     * 存储bean类名 与 {@link IBeanDefinition} 映射集合
     * <p>key: beanClassName , value: {@link IBeanDefinition}对象</p>
     */
    protected final Map<String, IBeanDefinition> beanClassNameBeanDefinitionMap = new ConcurrentHashMap<>(8);

    /**
     * 存储bean id 与 bean对象 映射集合
     * <p>key: beanId, value: {@link BeanObject}对象</p>
     */
    protected final Map<String, BeanObject> beanMap = new ConcurrentHashMap<>(8);

    /**
     * 存储bean类名与 bean对象 映射集合
     * <p>key: beanClass , value: {@link BeanObject}对象</p>
     */
    protected final Map<Class<?>, BeanObject[]> beanClassBeanMap = new ConcurrentHashMap<>(8);

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
        String className = beanDefinition.getBeanClassName();
        synchronized (this){
            // 判读是否已经存在相应的bean定义信息，如果存在，则抛出相应的异常
            IBeanDefinition beanDefinition1;
            if(!XLPStringUtil.isEmpty(beanId)){
                beanDefinition1 = beanIdBeanDefinitionMap.get(beanId);
                if (beanDefinition1 != null){
                    throw new BeanDefinitionExistException(beanId);
                }
                beanIdBeanDefinitionMap.put(beanId, beanDefinition);
            } else {
                beanDefinition1 = beanClassNameBeanDefinitionMap.get(className);
                if (beanDefinition1 != null){
                    throw new BeanDefinitionExistException(className);
                }
            }
            beanClassNameBeanDefinitionMap.put(className, beanDefinition);
        }
    }

    /**
     * 预先创建所有非延迟加载的单例bean
     * @see Component#lazy()
     * @throws BeanBaseException 假如创建bean实例失败，则抛出该异常或其子类异常
     */
    public void createBeans(){

    }

    /**
     * 根据bean定义创建bean对象
     * @param beanDefinition
     * @return 返回bean实例
     * @throws BeanBaseException 假如创建bean实例失败，则抛出该异常或其子类异常
     */
    protected Object createBean(IBeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton() && !beanDefinition.isLazy()
                && !beanDefinition.isAbstract()){
            return doCreateBean(beanDefinition);
        }
        return null;
    }

    /**
     * 根据bean定义创建bean对象
     * @param beanDefinition bean定义
     * @return 返回bean实例
     * @throws BeanBaseException 假如创建bean实例失败，则抛出该异常或其子类异常
     */
    protected Object doCreateBean(IBeanDefinition beanDefinition) {
        String beanId = beanDefinition.getBeanId();
        String className = beanDefinition.getBeanClassName();

        Object bean = beanMap.get(beanId);
        //假如缓存中存在该bean，则则直接返回
        if (bean != null) return bean;

        // 判断bean是否
        IBeanCreator beanCreator = beanDefinition.getBeanCreator();
        // 假如没有创建器，则直接跳过
        if (beanCreator == null) return null;
        // 获取bean实例，即为未给其他属性赋值的bean实例
        bean = beanCreator.createBean();
        IBeanField[] beanFields = beanDefinition.getBeanFields();
        //判断是否有属性要设置，有直接把半处理化bean放入临时缓存
        if (beanFields != null){
            for (IBeanField beanField : beanFields) {
                if (!beanField.hasSetMethod()) continue;
                //TODO
                String _beanId = beanField.getRefBeanId();
                String _className = beanField.getRefBeanClassName();
                //TODO 有问题
                bean = doCreateBean(BeanDefinitionFinder.find(beanField, beanIdBeanDefinitionMap,
                        beanClassNameBeanDefinitionMap));
            }
        }

        if(!XLPStringUtil.isEmpty(beanId)){
            //beanMap.put(beanId, bean);
            //beanClassNameBeanMap.put(className, bean);
        } else {

        }

        return bean;
    }

    /**
     * 向容器中添加指定类型的bean定义对象
     *
     * @param beanClass bean定义对象
     * @throws BeanDefinitionExistException 假如容器中存在，则抛出该异常
     * @throws NullPointerException 假如参数为空则抛出该异常
     * @see ComponentAnnotationBeanDefinition
     */
    @Override
    public void addBeanDefinition(Class<?> beanClass) throws BeanDefinitionExistException {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        Component component = beanClass.getAnnotation(Component.class);
        if (component != null) {
            this.addBeanDefinition(new ComponentAnnotationBeanDefinition(beanClass));
        } else {
            this.addBeanDefinition(new CustomRegisteredBeanDefinition(beanClass));
        }
    }

    /**
     * 判断bean定义已在容器中存在的类型
     *
     * @param beanDefinition
     * @return
     */
    @Override
    public BeanDefinitionExistType judgeBeanDefinition(IBeanDefinition beanDefinition) {
        if (beanDefinition == null) return BeanDefinitionExistType.NONE;
        String beanId = beanDefinition.getBeanId();
        String className = beanDefinition.getBeanClassName();
        if(!XLPStringUtil.isEmpty(beanId)){
            if (beanIdBeanDefinitionMap.get(beanId) != null){
                return BeanDefinitionExistType.BY_BEAN_ID;
            }
        }
        if (beanClassNameBeanDefinitionMap.get(className) != null){
            return BeanDefinitionExistType.BY_BEAN_CLASS_NAME;
        }
        return BeanDefinitionExistType.NONE;
    }

    /**
     * 向容器中添加指定ID的bean对象
     *
     * @param bean
     * @param beanId
     * @throws BeanExistException 假如容器中存指定ID的bean，则抛出该异常
     */
    @Override
    public <T> void addBean(T bean, String beanId) throws BeanExistException {

    }

    /**
     * 向容器中添加指定类型的bean
     *
     * @param bean
     * @param beanClass
     * @throws BeanExistException 假如容器中存指定类型的bean，则抛出该异常
     */
    @Override
    public <T> void addBean(T bean, Class<? super T> beanClass) throws BeanExistException {

    }

    /**
     * 判断是否有指定ID的bean
     *
     * @param beanId ID
     * @return true：有，false：没有
     */
    @Override
    public boolean hasBean(String beanId) {
        boolean has = beanMap.containsKey(beanId);
        if (has) return true;
        IBeanDefinition beanDefinition = beanIdBeanDefinitionMap.get(beanId);
        return beanDefinition != null && !beanDefinition.isAbstract();
    }

    /**
     * 判断是否有指定类型的bean
     *
     * @param beanClass
     * @return true：有，false：没有
     * @throws NullPointerException 假如参数为空，则抛出该异常
     */
    @Override
    public boolean hasBean(Class<?> beanClass) {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        String beanClassName = beanClass.getName();
        boolean has = beanClassBeanMap.containsKey(beanClass);
        if (has) return true;
        return true;
    }

    /**
     * 根据beanId或类型名称获取bean定义，假如没获取到统一抛出异常
     * @param beanId
     * @param className
     * @return
     * @throws NotSuchBeanException 假如未找到指定条件bean则抛出该异常
     */
    private IBeanDefinition getBeanDefinitionAndThrowNotSuchBeanException(String beanId, String className)
            throws NotSuchBeanException{
        IBeanDefinition beanDefinition = null;
        if (!XLPStringUtil.isEmpty(beanId)){
            beanDefinition = beanIdBeanDefinitionMap.get(beanId);
        } else if (!XLPStringUtil.isEmpty(className)){
            beanDefinition = beanClassNameBeanDefinitionMap.get(className);
        }
        if (beanDefinition == null){
            throw new NotSuchBeanException(XLPStringUtil.isEmpty(beanId) ? className : beanId);
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
        return getBeanDefinitionAndThrowNotSuchBeanException(id, null).isProxy();
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
        return getBeanDefinitionAndThrowNotSuchBeanException(null, className).isProxy();
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
        return getBeanDefinitionAndThrowNotSuchBeanException(id, null).isSingleton();
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
        return getBeanDefinitionAndThrowNotSuchBeanException(null, className).isSingleton();
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
        return getBeanDefinitionAndThrowNotSuchBeanException(id, null).isLazy();
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
        return getBeanDefinitionAndThrowNotSuchBeanException(null, className).isLazy();
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
     * @throws NullPointerException 假如参数为null，则抛出该异常
     */
    @Override
    public <T, I> T getBean(Class<I> beanClass) throws NotSuchBeanException {
        return getBean(beanClass, new Type[0]);
    }

    /**
     * 获取该id的bean对象
     *
     * @param beanClass bean类型
     * @param types     目标泛型类型
     * @return bean 对象
     * @throws BeanBaseException 假如或bean过程失败，则抛出该异常
     * @throws NullPointerException 假如第一个参数为null，则抛出该异常
     */
    @Override
    public <T, I> T getBean(Class<I> beanClass, Type[] types) throws BeanBaseException {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        BeanObject[] beanObjects = beanClassBeanMap.get(beanClass);
        if (XLPArrayUtil.isEmpty(beanObjects)){
            Set<BeanObject> beanObjectSet = new HashSet<>();
            beanClassBeanMap.forEach((key, value) -> {
                if (beanClass.isAssignableFrom(key)){
                    beanObjectSet.addAll(Arrays.asList(value));
                } else if(key.isAssignableFrom(beanClass)){
                    for (BeanObject beanObject : value) {
                        if (beanClass.isInstance(beanObject.getRawObject())){
                            beanObjectSet.add(beanObject);
                        }
                    }
                }
            });
            // 未找到，从Bean定义中去查找
            if (beanObjectSet.isEmpty()){

            } else {
                return getBean(beanClass, types, beanObjectSet.toArray(new BeanObject[0]));
            }
        } else {
            return getBean(beanClass, types, beanObjects);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T, I> T getBean(Class<I> beanClass, Type[] types, BeanObject[] beanObjects) {
        BeanObject beanObject = null;
        int count = 0;
        for (BeanObject object : beanObjects) {
            beanObject = object;
            if (beanObject.compareClassAndTypes(beanClass, types)) {
                count++;
            }
        }
        if (count == 0) {
            throw new NotSuchBeanException("未适配到类型为【" + beanClass.getName() + "】的bean实例！");
        }
        if (count > 1) {
            throw new MultiplyBeanException(beanClass);
        }
        return (T) beanObject.getRawObject();
    }

    /**
     * 获取该类全路径名称的bean对象
     *
     * @param className 类全路径名称
     * @return bean 对象
     * @throws BeanBaseException 假如或bean过程失败，则抛出该异常
     * @throws NullPointerException 假如参数为null或空，则抛出该异常
     */
    @Override
    public <T> T getBeanByClassName(String className) throws BeanBaseException {
        AssertUtils.isNotNull(className, "className parameter is null or empty!");
        return getBean(ClassForNameUtils.forName(className));
    }
}
