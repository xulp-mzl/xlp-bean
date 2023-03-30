package org.xlp.bean.util;

import org.xlp.bean.base.IBeanWrapper;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 提供一些获取需要忽略的数据工具类
 */
public class IgnoreUtils {
    /**
     * 保存Object类中所有方法的名称，用来在代理对象中忽略这些方法
     */
    private static final Set<String> IGNORE_METHOD_NAME_OF_OBJECT = new HashSet<>();

    /**
     * 保存{@link org.xlp.bean.base.IBeanWrapper}接口中所有方法的名称，用来在代理对象中忽略这些方法
     */
    private static final Set<String> IGNORE_METHOD_NAME_OF_BEAN_WRAPPER = new HashSet<>();

    static {
        // 初始化数据
        IGNORE_METHOD_NAME_OF_BEAN_WRAPPER.add(IBeanWrapper.AFTER_EXECUTE_METHOD_NAME);
        IGNORE_METHOD_NAME_OF_BEAN_WRAPPER.add(IBeanWrapper.THROW_EXECUTE_METHOD_NAME);
        IGNORE_METHOD_NAME_OF_BEAN_WRAPPER.add(IBeanWrapper.BEFORE_EXECUTE_METHOD_NAME);

        // 获取Object类中的方法
        Class<Object> objectClass = Object.class;
        Method[] methods = objectClass.getMethods();
        for (Method method : methods) {
            IGNORE_METHOD_NAME_OF_OBJECT.add(method.getName());
        }
    }

    /**
     * 判断在Object中是否存在给定方法名称的方法
     * @param methodName 方法名称
     * @return true：存在，false：不存在
     */
    public static boolean methodInObject(String methodName){
        return IGNORE_METHOD_NAME_OF_OBJECT.contains(methodName);
    }

    /**
     * 判断在IBeanWrapper中是否存在给定方法名称的方法
     * @param methodName 方法名称
     * @return true：存在，false：不存在
     */
    public static boolean methodInIBeanWrapper(String methodName){
        return IGNORE_METHOD_NAME_OF_BEAN_WRAPPER.contains(methodName);
    }
}
