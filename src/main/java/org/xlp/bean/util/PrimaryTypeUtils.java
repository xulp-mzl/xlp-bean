package org.xlp.bean.util;

import org.xlp.utils.XLPPackingTypeUtil;

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

    public static boolean isNormalType(Class<?> clazz){
        return true;
    }
}
