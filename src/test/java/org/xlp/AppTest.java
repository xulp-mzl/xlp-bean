package org.xlp;

import org.junit.Test;
import org.xlp.bean.proxy.XLPProxy;
import org.xlp.javabean.JavaBeanPropertiesDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void test() {
        List<String> list = new ArrayList<>();
        Class<AppTest> testClass = AppTest.class;
     //   System.out.println(AppTest.class.getConstructors());
        JavaBeanPropertiesDescriptor<?> pd = new JavaBeanPropertiesDescriptor<>(A.class);
        Field[] fields = pd.getFields();
        System.out.println(Arrays.toString(testClass.getGenericInterfaces()));
        for (Field field : fields) {
            System.out.println(field.getType());
            Type type = field.getGenericType();
            System.out.println(type.getTypeName() + "--" + type + "--" + type.getClass());
            if (type instanceof ParameterizedType){
                ParameterizedType pType = ((ParameterizedType) type);
                System.out.println(pType.getRawType());
                Type[] actualTypeArguments = pType.getActualTypeArguments();
                for (Type actualTypeArgument : actualTypeArguments) {
                    System.out.println(actualTypeArgument + "----" + actualTypeArgument.getClass() + "----" + actualTypeArgument.getTypeName());
                }
            }

            System.out.println("===================\n");
        }
    }

    @Test
    public void test1(){
        System.out.println(List.class.isInterface());
        Class<AbstractList> abstractListClass = AbstractList.class;
        System.out.println(abstractListClass.isInterface());
        System.out.println(Modifier.isAbstract(abstractListClass.getModifiers()));
        System.out.println(Modifier.isAbstract(List.class.getModifiers()));
    }

    @Test
    public void test2(){
        XLPProxy proxy = new XLPProxy(A.class);
        A<String> a = proxy.createProxy();
        a.fun2();
        a.fun4();
        a.fun6();
        System.out.println(Arrays.toString(A.class.getTypeParameters()));
    }
}
