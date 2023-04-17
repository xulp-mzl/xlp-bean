package org.xlp;

import org.junit.Test;
import org.xlp.bean.impl.DefaultBeansContainer;
import org.xlp.bean.proxy.CglibProxy;
import org.xlp.bean.util.ParameterizedTypeUtils;
import org.xlp.javabean.JavaBeanPropertiesDescriptor;

import java.lang.reflect.*;
import java.util.*;

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
                    if(actualTypeArgument instanceof WildcardType){
                        System.out.println(Arrays.toString(((WildcardType) actualTypeArgument).getLowerBounds()));
                        System.out.println(Arrays.toString(((WildcardType) actualTypeArgument).getUpperBounds()));
                    }
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
        CglibProxy proxy = new CglibProxy(A.class);
        A a = proxy.createProxy();
        a.fun2();
        a.fun4();
        a.fun6();
        System.out.println(Arrays.toString(A.class.getTypeParameters()));
    }

    @Test
    public <Z, C> void test3(){
        DefaultBeansContainer beansContainer = new DefaultBeansContainer();
        Type[] classTypes = ParameterizedTypeUtils.getClassTypes(B.class);
        System.out.println(Arrays.toString(classTypes));
        List<Z> list = new ArrayList<>();
        List<List<C>> integers = new ArrayList<>();
        list = (List<Z>) integers;

    }

    @Test
    public void test4(){
        Integer i = 2;
        System.out.println(Integer.class.isInstance(null));
        Map<String, String> map = new HashMap<>();
        map.put("kk", "23");
        map.put("zz", "45");
        System.out.println(map);
        System.out.println(map.computeIfAbsent("kk", (key) -> {
            return "25";
        }));
        System.out.println(map);
        System.out.println(map.computeIfAbsent("xx", (key) -> {
            return "25";
        }));
        System.out.println(map);

        System.out.println(map.compute("kk", (key, value) -> {
            System.out.println(value);
            return "55";
        }));
        System.out.println(map);

        System.out.println(map.compute("66", (key, value) -> {
            System.out.println(value);
            return "55";
        }));
        System.out.println(map);
    }

    @Test
    public void test5(){
        Optional<String> str = Optional.ofNullable(null);
        str.ifPresent((str1) -> {
            throw new RuntimeException("25");
        });
        DefaultBeansContainer container = new DefaultBeansContainer();
        System.out.println((Object) container.getBean(C.class));
    }
}
