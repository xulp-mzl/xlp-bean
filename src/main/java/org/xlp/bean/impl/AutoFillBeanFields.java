package org.xlp.bean.impl;

import org.xlp.assertion.AssertUtils;
import org.xlp.bean.annotation.AutoFill;
import org.xlp.bean.base.IBeanField;
import org.xlp.bean.base.IBeanFields;
import org.xlp.javabean.JavaBeanPropertiesDescriptor;
import org.xlp.javabean.PropertyDescriptor;

import java.lang.reflect.Type;

public class AutoFillBeanFields implements IBeanFields {
    /**
     * bean类型
     */
    private final Class<?> beanClass;

    /**
     * 构造函数
     * @param beanClass bean类型
     * @throws NullPointerException 假如参数为null则抛出该异常
     */
    public AutoFillBeanFields(Class<?> beanClass){
        AssertUtils.isNotNull(beanClass, "beanClass parameter is null!");
        this.beanClass = beanClass;
    }

    /**
     * 获取IBeanField对象
     *
     * @return
     */
    @Override
    public IBeanField[] getBeanFields() {
        JavaBeanPropertiesDescriptor<?> jpd = new JavaBeanPropertiesDescriptor<>(beanClass);
        // 获取AutoFill注解的字段描述
        PropertyDescriptor<?>[] pds = jpd.getPdsWithAnnotation(AutoFill.class);
        int len = pds.length;
        IBeanField[] beanFields = new IBeanField[len];
        for (int i = 0; i < len; i++) {
            beanFields[i] = new AutoFillBeanField(pds[i]);
        }
        return beanFields;
    }

    /**
     * 获取bean类型
     *
     * @return
     */
    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    public static class AutoFillBeanField implements IBeanField{
        /**
         * 字段类型
         */
        private Class<?> fieldType;

        /**
         * 字段名称
         */
        private String name;

        /**
         * 私有构造函数，防止外部实例化该对象
         * @param pd 字段描述
         */
        private AutoFillBeanField(PropertyDescriptor<?> pd){
            this.fieldType = pd.getFiledClassType();
            this.name = pd.getFieldName();
        }

        /**
         * 判断指定属性是否是基本类型
         */
        @Override
        public boolean isPrimary() {
            return false;
        }

        /**
         * 判断指定属性是否是数组
         */
        @Override
        public boolean isArray() {
            return fieldType.isArray();
        }

        /**
         * 字段名称
         *
         * @return
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * 获取该字段引用的bean Id
         *
         * @return
         * @see AutoFill#refId
         */
        @Override
        public String getRefBeanId() {
            return null;
        }

        /**
         * 获取该字段引用的bean Id
         *
         * @return
         * @see AutoFill#refClassName
         */
        @Override
        public String getRefBeanClassName() {
            return null;
        }

        /**
         * 获取该字段是否有相应的set函数
         *
         * @return
         */
        @Override
        public boolean hasSetMethod() {
            return false;
        }

        /**
         * 获取字段类别
         *
         * @return
         */
        @Override
        public Class<?> getFieldClass() {
            return null;
        }

        /**
         * 获取泛型信息
         *
         * @return
         */
        @Override
        public Type[] getActualType() {
            return new Type[0];
        }
    }
}
