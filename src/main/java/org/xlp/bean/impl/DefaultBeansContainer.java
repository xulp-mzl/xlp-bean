package org.xlp.bean.impl;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.annotation.Component;
import org.xlp.bean.base.IBeanCreator;
import org.xlp.bean.base.IBeanDefinition;
import org.xlp.bean.base.IBeanField;
import org.xlp.bean.base.IBeansContainer;
import org.xlp.bean.exception.*;
import org.xlp.bean.object.BeanObject;
import org.xlp.bean.util.ClassForNameUtils;
import org.xlp.bean.util.MethodUtils;
import org.xlp.bean.util.ParameterizedTypeUtils;
import org.xlp.utils.XLPArrayUtil;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.collection.XLPCollectionUtil;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean 容器的默认实现
 */
public class DefaultBeansContainer implements IBeansContainer {
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
     * 存储bean id 与 bean对象(未给字段设置值的对象) 映射集合
     * <p>key: beanId, value: {@link BeanObject}对象</p>
     */
    protected final Map<String, BeanObject> beanHalfMap = new ConcurrentHashMap<>(8);

    /**
     * 存储bean类名与 bean对象(未给字段设置值的对象) 映射集合
     * <p>key: beanClass , value: {@link BeanObject}对象</p>
     */
    protected final Map<Class<?>, BeanObject[]> beanClassHalfBeanMap = new ConcurrentHashMap<>(8);

    /**
     * 存储当前线程创建一个bean时，所有关联的bean类型，用来判断是否存在循环依赖
     */
    private final static ThreadLocal<Set<Class<?>>> CURRENT_THREAD_USED_CLASS = new ThreadLocal<>();

    /**
     * 标记是否正在重置容器中
     */
    private boolean resetting = false;

    /**
     * 向容器中添加bean定义对象
     *
     * @param beanDefinition bean定义对象
     * @param covering       是否覆盖已有的bean定义，true；是  false: 否
     * @throws BeanDefinitionExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     * @throws NullPointerException 假如参数为空则抛出该异常
     */
    @Override
    public void addBeanDefinition(IBeanDefinition beanDefinition, boolean covering) throws BeanDefinitionExistException {
        AssertUtils.isNotNull(beanDefinition, "beanDefinition parameter is null!");
        String beanId = beanDefinition.getBeanId();
        String className = beanDefinition.getBeanClassName();
        synchronized (this){
            // 判读是否已经存在相应的bean定义信息，如果存在，则抛出相应的异常
            IBeanDefinition beanDefinition1;
            if(!XLPStringUtil.isEmpty(beanId)){
                beanDefinition1 = beanIdBeanDefinitionMap.get(beanId);
                if (beanDefinition1 != null && !covering){
                    throw new BeanDefinitionExistException(beanId);
                }
                beanIdBeanDefinitionMap.put(beanId, beanDefinition);
            } else {
                beanDefinition1 = beanClassNameBeanDefinitionMap.get(className);
                if (beanDefinition1 != null && !covering){
                    throw new BeanDefinitionExistException(className);
                }
            }
            beanClassNameBeanDefinitionMap.put(className, beanDefinition);
        }
    }

    /**
     * 预先创建所有非延迟加载的单例bean, 执行该方法会先去重置容器
     * @see Component#lazy()
     * @throws BeanBaseException 假如创建bean实例失败，则抛出该异常或其子类异常
     */
    public synchronized void createBeans(){
      reset();
      beanClassNameBeanDefinitionMap.forEach((key, value) -> createBean(value));
    }

    /**
     * 根据bean定义创建bean对象
     * @param beanDefinition
     * @return 返回bean实例
     * @throws BeanBaseException 假如创建bean实例失败，则抛出该异常或其子类异常
     */
    @SuppressWarnings("UnusedReturnValue")
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
    private Object doCreateBean(IBeanDefinition beanDefinition) {
        String beanId = beanDefinition.getBeanId();
        Class<?> beanClass = beanDefinition.getBeanClass();
        boolean beanIdIsBlank = XLPStringUtil.isEmpty(beanId);

        // 判断bean是否
        IBeanCreator beanCreator = beanDefinition.getBeanCreator();
        // 假如没有创建器，则直接跳过
        if (beanCreator == null) return null;
        // 获取bean实例，即为未给其他属性赋值的bean实例
        Object bean = beanCreator.createBean();
        BeanObject beanObject = new BeanObject(beanDefinition, bean);
        //检验是否存在循环依赖
        checkExitsCircularDependency(beanClass);
        addBeanClassToThreadLocal(beanClass);
        boolean hasException = false;
        try {
            if(beanDefinition.isSingleton()) {
                if (!beanIdIsBlank) {
                    beanObject = beanHalfMap.putIfAbsent(beanId, beanObject);
                }
                assert beanObject != null;
                beanObject = addBeanObjectToBeanMap(beanObject, beanClassHalfBeanMap);
            }

            //获取bean要注入的属性
            IBeanField[] beanFields = beanDefinition.getBeanFields();
            if (beanFields != null){
                for (IBeanField beanField : beanFields) {
                    //跳过不是字段适配的属性
                    if (beanField.isArray() || beanField.isPrimary() || !beanField.hasSetMethod()){
                        continue;
                    }
                    initBeanAttribute(bean, beanField);
                }
            }
        } catch (Throwable e){
            hasException = true;
            throw e;
        } finally {
            if(beanDefinition.isSingleton()) {
                synchronized (this) {
                    if (!beanIdIsBlank) {
                        beanHalfMap.remove(beanId);
                    }
                    removeBeanClassFromHalfBeanMap(beanClass, beanObject);

                    if (!hasException) {
                        if (!beanIdIsBlank) {
                            beanObject = beanMap.putIfAbsent(beanId, beanObject);
                        }
                        assert beanObject != null;
                        beanObject = addBeanObjectToBeanMap(beanObject, beanClassBeanMap);
                    }
                }
            }
            removeBeanClassToThreadLocal(beanClass);
        }
        return beanObject.getRawObject();
    }

    private void removeBeanClassFromHalfBeanMap(Class<?> beanClass, BeanObject beanObject) {
        BeanObject[] beanObjects = beanClassHalfBeanMap.get(beanClass);
        if (XLPArrayUtil.isEmpty(beanObjects)) {
            beanClassHalfBeanMap.remove(beanClass);
            return;
        }
        HashSet<BeanObject> objects = new HashSet<>(Arrays.asList(beanObjects));
        objects.remove(beanObject);
        if (objects.isEmpty()){
            beanClassHalfBeanMap.remove(beanClass);
            return;
        }
        beanClassHalfBeanMap.put(beanClass, objects.toArray(new BeanObject[0]));
    }

    private void initBeanAttribute(Object bean, IBeanField beanField) {
        String fieldId = beanField.getRefBeanId();
        String fieldRefClassName = beanField.getFieldClassName();
        Object fieldBean;
        if (!XLPStringUtil.isEmpty(fieldId)){
            fieldBean = _getBean(fieldId);
        } else {
            boolean fieldRefClassNameIsEmpty = XLPStringUtil.isEmpty(fieldRefClassName);
            Class<?> beanClass = fieldRefClassNameIsEmpty ? beanField.getFieldClass()
                    : ClassForNameUtils.forName(fieldRefClassName);
            Type[] types = fieldRefClassNameIsEmpty ? beanField.getActualType() : new Type[0];
            fieldBean = _getBean(beanClass, types);
        }
        if (beanField.isRequired() && fieldBean == null){
            throw new BeanBaseException("为找到【" + beanField.getBeanClass() + "." + beanField.getName()
                    + "属性对应的bean，装配属性失败。");
        }
        //设置bean属性
        MethodUtils.invoke(bean, beanField, fieldBean);
    }

    private void removeBeanClassToThreadLocal(Class<?> beanClass){
        Set<Class<?>> classes = CURRENT_THREAD_USED_CLASS.get();
        if (XLPCollectionUtil.isEmpty(classes)){
            CURRENT_THREAD_USED_CLASS.remove();
        }
        classes.remove(beanClass);
    }

    private void addBeanClassToThreadLocal(Class<?> beanClass){
        Set<Class<?>> classes = CURRENT_THREAD_USED_CLASS.get();
        if (classes == null){
            classes = new HashSet<>();
        }
        classes.add(beanClass);
        CURRENT_THREAD_USED_CLASS.set(classes);
    }

    /**
     * 校验bean之间是否存在循环依赖问题
     * @param beanClass
     * @throws CircularDependencyException 假如存在循环依赖则抛出该异常
     */
    protected void checkExitsCircularDependency(Class<?> beanClass){
        Optional.ofNullable(CURRENT_THREAD_USED_CLASS.get())
            .ifPresent(classSet -> {
                if (classSet.contains(beanClass)){
                    throw new CircularDependencyException(CircularDependencyException.getCircularDependencyMsg(classSet));
                }
            });
    }

    private BeanObject addBeanObjectToBeanMap(BeanObject beanObject,  Map<Class<?>, BeanObject[]> optionBeanMap) {
        synchronized (this){
            Class<?> beanClass = beanObject.getBeanClass();
            BeanObject[] beanObjects = optionBeanMap.get(beanClass);
            if (beanObjects != null) {
                BeanObject _beanObject = getBeanObject(beanObject.getBeanClass(), beanObject.getTypes(),
                        new HashSet<>(Arrays.asList(beanObjects)), false);
                if (_beanObject != null){
                    return _beanObject;
                }
                int len = beanObjects.length;
                beanObjects = Arrays.copyOf(beanObjects, len + 1);
                beanObjects[len] = beanObject;
            } else {
                beanObjects = new BeanObject[]{beanObject};
            }
            optionBeanMap.put(beanClass, beanObjects);
            return beanObject;
        }
    }

    /**
     * 向容器中添加指定类型的bean定义对象
     *
     * @param beanClass bean定义对象
     * @param covering  是否覆盖已有的bean定义，true；是  false: 否
     * @throws BeanDefinitionExistException 假如容器中存在并且<dode>covering is false</dode>，则抛出该异常
     * @throws NullPointerException 假如参数为空则抛出该异常
     * @see ComponentAnnotationBeanDefinition
     */
    @Override
    public void addBeanDefinition(Class<?> beanClass, boolean covering) throws BeanDefinitionExistException {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        Component component = beanClass.getAnnotation(Component.class);
        if (component != null) {
            this.addBeanDefinition(new ComponentAnnotationBeanDefinition(beanClass), covering);
        } else {
            this.addBeanDefinition(new CustomClassOfBeanDefinition(beanClass), covering);
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
     * 重置容器中的数据
     */
    @Override
    public void reset() {
        synchronized (this){
            resetting = true;
            beanClassHalfBeanMap.clear();
            beanHalfMap.clear();
            beanMap.clear();
            beanClassBeanMap.clear();
            beanIdBeanDefinitionMap.clear();
            beanClassNameBeanDefinitionMap.clear();
            resetting = false;
        }
        CURRENT_THREAD_USED_CLASS.remove();
    }

    private NotSuchBeanException getNotSuchBeanException(String beanId, String className){
        String msg = XLPStringUtil.isEmpty(beanId) ? "[" + className + "]该类型的bean未找到"
                : "[" + beanId + "]该id的bean未找到";
        throw new NotSuchBeanException(msg);
    }

    /**
     * 判断给的id的bean是否被代理
     *
     * @param id bean id
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     * @throws NullPointerException 假如参数为null或空，则抛出该异常
     */
    @Override
    public boolean isProxy(String id) throws NotSuchBeanException {
        AssertUtils.isNotNull(id, "id parameter is null or empty!");
        IBeanDefinition beanDefinition = beanIdBeanDefinitionMap.get(id);
        if (beanDefinition != null) return beanDefinition.isProxy();
        BeanObject beanObject = beanMap.get(id);
        if (beanObject != null) return false;
        throw getNotSuchBeanException(id, null);
    }

    /**
     * 判断给的类型的bean是否被代理
     *
     * @param beanClass bean类型
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定类名的bean则抛出该异常
     * @throws NullPointerException 假如参数为null，则抛出该异常
     */
    @Override
    public <T> boolean isProxy(Class<T> beanClass) throws NotSuchBeanException {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        IBeanDefinition beanDefinition = getBeanDefinition(beanClass);
        if (beanDefinition != null) return beanDefinition.isProxy();
        Set<BeanObject> beanObjects = getBeanObjects(beanClass, beanClassBeanMap);
        if (!XLPCollectionUtil.isEmpty(beanObjects)) return false;
        throw getNotSuchBeanException(null, beanClass.getName());
    }

    private <T> IBeanDefinition getBeanDefinition(Class<T> beanClass){
        int count = 0;
        IBeanDefinition beanDefinition = null;
        for (Map.Entry<String, IBeanDefinition> entry : beanClassNameBeanDefinitionMap.entrySet()) {
            beanDefinition = entry.getValue();
            if (beanClass.isAssignableFrom(beanDefinition.getBeanClass())){
                count++;
            }
        }
        if (count > 1){
           throw new MultiplyBeanException(beanClass);
        }
        return beanDefinition;
    }

    /**
     * 判断给的id的bean是否是单例
     *
     * @param id bean id
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     * @throws NullPointerException 假如参数为null或空，则抛出该异常
     */
    @Override
    public boolean isSingleton(String id) throws NotSuchBeanException {
        AssertUtils.isNotNull(id, "id parameter is null or empty!");
        IBeanDefinition beanDefinition = beanIdBeanDefinitionMap.get(id);
        if (beanDefinition != null) return beanDefinition.isSingleton();
        BeanObject beanObject = beanMap.get(id);
        if (beanObject != null) return true;
        throw getNotSuchBeanException(id, null);
    }

    /**
     * 判断给的类型的bean是否是单例
     *
     * @param beanClass bean类型
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     * @throws NullPointerException 假如参数为null，则抛出该异常
     */
    @Override
    public <T> boolean isSingleton(Class<T> beanClass) throws NotSuchBeanException {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        IBeanDefinition beanDefinition = getBeanDefinition(beanClass);
        if (beanDefinition != null) return beanDefinition.isSingleton();
        Set<BeanObject> beanObjects = getBeanObjects(beanClass, beanClassBeanMap);
        if (!XLPCollectionUtil.isEmpty(beanObjects)) return true;
        throw getNotSuchBeanException(null, beanClass.getName());
    }

    /**
     * 判断给的id的bean是否延迟实例化
     *
     * @param id bean id
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     * @throws NullPointerException 假如参数为null或空，则抛出该异常
     */
    @Override
    public boolean isLazy(String id) throws NotSuchBeanException {
        AssertUtils.isNotNull(id, "id parameter is null or empty!");
        IBeanDefinition beanDefinition = beanIdBeanDefinitionMap.get(id);
        if (beanDefinition != null) return beanDefinition.isLazy();
        BeanObject beanObject = beanMap.get(id);
        if (beanObject != null) return false;
        throw getNotSuchBeanException(id, null);
    }

    /**
     * 判断给的类型的bean是否延迟实例化
     *
     * @param beanClass bean类型
     * @return true: 是，false：否
     * @throws NotSuchBeanException 假如未找到指定id的bean则抛出该异常
     * @throws NullPointerException 假如参数为null，则抛出该异常
     */
    @Override
    public <T> boolean isLazy(Class<T> beanClass) throws NotSuchBeanException {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        IBeanDefinition beanDefinition = getBeanDefinition(beanClass);
        if (beanDefinition != null) return beanDefinition.isLazy();
        Set<BeanObject> beanObjects = getBeanObjects(beanClass, beanClassBeanMap);
        if (!XLPCollectionUtil.isEmpty(beanObjects)) return false;
        throw getNotSuchBeanException(null, beanClass.getName());
    }

    /**
     * 从容器中相应id的bean对象
     *
     * @param beanId beanId
     * @return
     * @throws NotSuchBeanException
     * @throws NullPointerException 假如参数为null或空，则抛出该异常
     */
    @Override
    public <T> T getBean(String beanId) throws NotSuchBeanException {
        AssertUtils.isNotNull(beanId, "beanId parameter is null or empty!");
        //判断容器是否在重置中
        checkContainerResetting();
        T bean = _getBean(beanId);
        if (bean == null){
            throw new NotSuchBeanException("未适配到id为【" + beanId + "】的bean实例！");
        }
        //防止获取半初始化的bean对象
        awaitRemovedBeanIdFromCache(beanId);
        return bean;
    }

    /**
     * 等待未完成初始化的对象从缓存中移除
     * @param beanId
     */
    private void awaitRemovedBeanIdFromCache(String beanId) {
        BeanObject beanObject;
        do {
            beanObject = beanHalfMap.get(beanId);
        } while (beanObject != null);
    }

    /**
     * 从容器中相应id的bean对象
     *
     * @param beanId beanId
     * @return
     * @throws NotSuchBeanException
     */
    @SuppressWarnings("unchecked")
    private  <T> T _getBean(String beanId) throws NotSuchBeanException {
        BeanObject beanObject = null;
        if (!XLPStringUtil.isEmpty(beanId)){
            beanObject = beanMap.get(beanId);

            if (beanObject == null){
                // 未找到再次重bean定义中查找
                IBeanDefinition beanDefinition = beanIdBeanDefinitionMap.get(beanId);
                if (beanDefinition != null){
                    if (beanDefinition.isSingleton()){
                        synchronized (beanDefinition.getBeanClass()){
                            beanObject = beanMap.get(beanId);
                            return (T) (beanObject == null ? doCreateBean(beanDefinition) : beanObject.getRawObject());
                        }
                    }
                    return (T) doCreateBean(beanDefinition);
                }
            }
        }

        return (beanObject == null ? null : (T) beanObject.getRawObject());
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
        return getBean(beanClass, ParameterizedTypeUtils.getClassTypes(beanClass, true));
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
        //判断容器是否在重置中
        checkContainerResetting();

        T bean = _getBean(beanClass, types);
        if (bean == null){
            throw new NotSuchBeanException("未适配到类型为【" + beanClass.getName() + getTypesDescription(types)
                    + "】的bean实例！");
        }
        //防止获取半初始化的bean对象
        awaitRemovedBeanClassFromCache(beanClass, types);
        return bean;
    }

    private void checkContainerResetting() {
        if (resetting){
            throw new BeanBaseException("容器正在重置中，获取bean失败！");
        }
    }

    private <I> void awaitRemovedBeanClassFromCache(Class<I> beanClass, Type[] types) {
        BeanObject[] beanObjects = beanClassHalfBeanMap.get(beanClass);
        if (XLPArrayUtil.isEmpty(beanObjects)){
            return;
        }
        BeanObject beanObject;
        do {
            beanObject = getBeanObject(beanClass, types, new HashSet<>(Arrays.asList(beanObjects)), false);
        } while (beanObject != null);
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
    @SuppressWarnings("unchecked")
    private  <T, I> T _getBean(Class<I> beanClass, Type[] types) throws BeanBaseException {
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        Set<BeanObject> beanObjectSet = getBeanObjects(beanClass, beanClassBeanMap);
        BeanObject beanObject = getBeanObject(beanClass, types, beanObjectSet, false);

        if (beanObject == null){
            // 未找到，从Bean定义中去查找
            beanObjectSet = doGetBeanObjects(beanClass);
            beanObject = getBeanObject(beanClass, types, beanObjectSet, false);
            if (beanObject == null){
                return null;
            }
            IBeanDefinition beanDefinition = beanObject.getBeanDefinition();
            // 忽略 beanDefinition 为null的情况
            if (beanDefinition != null){
                Object bean;
                if (beanDefinition.isSingleton()){
                    synchronized (beanDefinition.getBeanClass()){
                        // 防止单例被创建多个实例对象
                        beanObjectSet = getBeanObjects(beanClass, beanClassBeanMap);
                        beanObject = getBeanObject(beanClass, types, beanObjectSet, false);
                        bean = beanObject == null ? doCreateBean(beanDefinition) : beanObject.getRawObject();
                    }
                } else {
                    bean = doCreateBean(beanDefinition);
                }
                return (T) bean;
            }
        }

        return (T) beanObject.getRawObject();
    }

    private String getTypesDescription(Type[] types){
        StringBuilder sb = new StringBuilder();
        boolean start = true;
        types = types == null ? new Type[0] : types;
        for (Type type : types) {
            if (!start){
                sb.append(",");
            }
            sb.append(type.getTypeName());
            start = false;
        }
        return sb.length() == 0 ? XLPStringUtil.EMPTY : "-<" + sb.toString() + ">";
    }

    /**
     * 获取指定类型待泛型目标类型的Bean对象
     *
     * @param beanClass bean类型
     * @param types     泛型目标类型
     * @return
     * @throws BeanBaseException 假如或bean过程失败，则抛出该异常
     */
    @Override
    public <T> T getBean(Class<?> beanClass, Class<?>... types) {
        Type[] _types = new Type[types == null ? 0 : types.length];
        for (int i = 0, len = _types.length; i < len; i++) {
            _types[i] = types[i];
        }
        return getBean(beanClass, _types);
    }

    private Set<BeanObject> getBeanObjects(Class<?> beanClass, Map<Class<?>, BeanObject[]> beanClassBeanMap) {
        Set<BeanObject> beanObjectSet = new HashSet<>();
        beanClassBeanMap.forEach((key, value) -> {
            if (beanClass.isAssignableFrom(key)){
                beanObjectSet.addAll(Arrays.asList(value));
            }
        });
        return beanObjectSet;
    }

    private Set<BeanObject> doGetBeanObjects(Class<?> beanClass){
        String beanClassName = beanClass.getName();
        Set<BeanObject> beanObjectList = new HashSet<>();
        beanClassNameBeanDefinitionMap.forEach((key, value) -> {
            if (beanClass.isAssignableFrom(value.getBeanClass())
                && !value.isAbstract()){
                beanObjectList.add(new BeanObject(value, (Object) null));
            }
        });
        return beanObjectList;
    }

    private BeanObject getBeanObject(Class<?> beanClass, Type[] types, Set<BeanObject> beanObjects,
                                     boolean notFindBeanThenThrowException) {
        BeanObject beanObject = null;
        int count = 0;
        for (BeanObject object : beanObjects) {
            beanObject = object;
            if (beanObject.compareClassAndTypes(beanClass, types)) {
                count++;
            }
        }
        if (count == 0 && notFindBeanThenThrowException) {
            throw new NotSuchBeanException("未适配到类型为【" + beanClass.getName() + getTypesDescription(types)
                    + "】的bean实例！");
        }
        if (count > 1) {
            throw new MultiplyBeanException(beanClass);
        }
        return beanObject;
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
