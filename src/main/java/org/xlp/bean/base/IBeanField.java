package org.xlp.bean.base;

/**
 * bean属性接口
 * <p>用来处理{@link org.xlp.bean.annotation.Component}标记类中{@link org.xlp.bean.annotation.AutoFill}标记的字段接口</p>
 */
public interface IBeanField extends IParameterizedType{
    /**
     * 判断指定属性是否是基本类型
     */
    boolean isPrimary();

    /**
     * 判断指定属性是否是数组
     */
    boolean isArray();

    /**
     * 字段名称
     * @return
     */
    String getName();

    /**
     * 获取该字段引用的bean Id
     * @see org.xlp.bean.annotation.AutoFill#refId
     * @return
     */
    String getRefBeanId();

    /**
     * 获取该字段引用的bean Id
     * @see org.xlp.bean.annotation.AutoFill#refClassName
     * @return
     */
    String getRefBeanClassName();

    /**
     * 获取该字段是否有相应的set函数
     * @return
     */
    boolean hasSetMethod();

    /**
     * 获取字段类别
     * @return
     */
    Class<?> getFieldClass();

    /**
     * 获取字段类型全路径名称
     * @return
     */
    default String getFieldClassName(){
        Class<?> fieldClass = getFieldClass();
        return fieldClass == null ? null : fieldClass.getName();
    }
}
