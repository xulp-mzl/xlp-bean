package org.xlp.bean.util;

import org.xlp.utils.XLPPackingTypeUtil;

import java.util.Collection;
import java.util.Map;

/**
 * 基础类型和常用类型判断工具类
 */
public class PrimaryTypeUtils {
    /**
     * 判读给的了类型是否是基础类型或包装类型
     * @param clazz 要判处的类型
     * @return true：是，false：不是
     */
    public static boolean isPrimaryType(Class<?> clazz){
        return XLPPackingTypeUtil.isPackingType(clazz)
                || XLPPackingTypeUtil.isRawNumberType(clazz)
                || XLPPackingTypeUtil.isOtherRawOrPackingType(clazz);
    }

    /**
     * 判断给定的类型是否是普通类型
     * <p>例如是否是集合类型或数字类型或字符串类型</p>
     *
     * @param clazz clazz 要判处的类型
     * @return true：是，false：不是
     */
    public static boolean isNormalType(Class<?> clazz){
        return clazz != null && (CharSequence.class.isAssignableFrom(clazz)
                || Collection.class.isAssignableFrom(clazz)
                || Map.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz)
                || Runtime.class.isAssignableFrom(clazz)
                || clazz.isEnum());
    }
}
