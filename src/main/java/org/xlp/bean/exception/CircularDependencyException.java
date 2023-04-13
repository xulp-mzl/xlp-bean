package org.xlp.bean.exception;

import org.xlp.utils.collection.XLPCollectionUtil;

import java.util.Set;

/**
 * 循环依赖问题
 */
public class CircularDependencyException extends BeanBaseException{
    private static final long serialVersionUID = -842125321623834138L;

    public CircularDependencyException(String message) {
        super(message);
    }

    /**
     * 获取循环依赖错误提示信息
     * @param classSet
     * @return
     * @throws IllegalArgumentException 假如参数为null或为空集合，则抛出该异常
     */
    public static String getCircularDependencyMsg(Set<Class<?>> classSet){
        if (XLPCollectionUtil.isEmpty(classSet)){
            throw new IllegalArgumentException("classSet parameter not be null or empty!");
        }
        StringBuilder sb = new StringBuilder();
        for (Class<?> aClass : classSet) {
            sb.append(aClass.getName()).append(",");
        }
        if (classSet.size() == 0){
            return "[" + sb.toString() + "]存在自己引用自己的循环依赖问题！";
        }
        return "[" + sb.toString() + "]之间存在循环依赖问题！";
    }
}
