package org.xlp.bean.util;

import org.xlp.utils.XLPArrayUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * 获取泛型信息工具类
 */
public class ParameterizedTypeUtils {
    /**
     * 获取指定类父类或父接口的泛型信息，找到了泛型信息，就停止向上找了
     * @param clazz
     * @return
     */
    public static Type[] getClassTypes(Class<?> clazz){
        Type[] types = new Type[0];
        if (clazz == null){
            return types;
        }

        Type type = clazz.getGenericSuperclass();

        if(type instanceof ParameterizedType){
            return ((ParameterizedType) type).getActualTypeArguments();
        }

        //超类中不存在泛型信息，则同接口中获取
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if(genericInterface instanceof ParameterizedType){
                return ((ParameterizedType) genericInterface).getActualTypeArguments();
            }
        }

        if (XLPArrayUtil.isEmpty(types)){
            //父类没找到再从父类的父类中查找，直到Object.class
            if ((type instanceof Class) && type != Object.class){
                types = getClassTypes((Class<?>) type);
            }
            if (XLPArrayUtil.isEmpty(types)){
                //还没找到从接口的父接口查找
                for (Type genericInterface : genericInterfaces) {
                    if (genericInterface instanceof Class){
                        types = getClassTypes((Class<?>) genericInterface);
                        if (!XLPArrayUtil.isEmpty(types)) return types;
                    }
                }
            }
        }

        return types;
    }

    /**
     * 比较两个泛型信息是否相同
     * @param types1
     * @param types2
     * @return true: 相同，false：不同
     */
    public static boolean equalsTypes(Type[] types1, Type[] types2){
        boolean empty1 = XLPArrayUtil.isEmpty(types1);
        boolean empty2 = XLPArrayUtil.isEmpty(types2);
        if (empty1 && empty2){
            return true;
        }
        if (empty1 || empty2){
            return false;
        }

        int len = types1.length;
        // 判断泛型信息长度是否相同，不相同则返回false
        if (len != types2.length){
            return false;
        }

        Type type1, type2;
        for (int i = 0; i < len; i++) {
            type1 = types1[i];
            type2 = types2[i];

            if (type1.equals(type2)){
                continue;
            }

            if ((type1 instanceof TypeVariable) && (type2 instanceof TypeVariable)){
                continue;
            }

            if (type1 instanceof WildcardType){
                WildcardType wildcardType = ((WildcardType) type1);
                Type[] lowerBounds = wildcardType.getLowerBounds();
                Type[] upperBounds = wildcardType.getUpperBounds();
                if (type2 instanceof Class){
                    if (lowerBounds.length == 1 && lowerBounds[0] instanceof Class){
                        return ((Class<?>) type2).isAssignableFrom(((Class<?>) lowerBounds[0]));
                    }
                    if (upperBounds.length == 1 && upperBounds[0] instanceof Class){
                        return ((Class<?>) upperBounds[0]).isAssignableFrom(((Class<?>) type2));
                    }
                } else if (type2 instanceof WildcardType){
                    Type[] lowerBounds2 = wildcardType.getLowerBounds();
                    Type[] upperBounds2 = wildcardType.getUpperBounds();
                    if (lowerBounds.length > 0 && lowerBounds2.length == lowerBounds.length){
                        return equalsTypes(lowerBounds, lowerBounds2);
                    }
                    if (upperBounds.length > 0 && upperBounds2.length == upperBounds.length){
                        return equalsTypes(upperBounds, upperBounds2);
                    }
                }
            }

            if ((type1 instanceof ParameterizedType) && (type2 instanceof ParameterizedType)){
                return equalsTypes(((ParameterizedType) type1).getActualTypeArguments(),
                        ((ParameterizedType) type2).getActualTypeArguments());
            }

            return false;
        }

        return true;
    }
}
