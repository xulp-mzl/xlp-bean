package org.xlp.bean.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.xlp.assertion.AssertUtils;
import org.xlp.bean.base.IBeanWrapper;
import org.xlp.bean.util.IgnoreUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 代理类生成类
 * 
 * @author 徐龙平
 * @version 1.0
 * 
 */
public class XLPProxy implements MethodInterceptor{
	/**
	 * 要被代理的类型
	 */
	private Class<?> targetClass;

	/**
	 * 根据要代理的类创建代理对象
	 * 
	 * @param class1 要代理对象的类型
	 * @return 代理对象
	 * @throws NullPointerException 假如参数为null则抛出该异常
	 */
	@SuppressWarnings("unchecked")
	public <T> T createProxy(Class<?> class1){
		AssertUtils.isNotNull(class1, "class1 parameter is null!");
		this.targetClass = class1;
		Enhancer enhancer = new Enhancer();
		enhancer.setClassLoader(class1.getClassLoader());
		enhancer.setSuperclass(class1);
		enhancer.setCallback(this);
		return (T) enhancer.create();
	}
	
	 /**
	 * 在代理实例上处理方法调用并返回结果
	 * 
	 * @param object
	 *            代理对象
	 * @param method
	 *            被代理的方法
	 * @param params
	 *            该方法的参数数组
	 * @param methodProxy 
	 * 			     代理方法的MethodProxy对象。每个被代理的方法都对应一个MethodProxy对象
	 * @return 方法返回值
	 */
	@Override
	public Object intercept(Object object, Method method, Object[] params,
			MethodProxy methodProxy) throws Throwable {
		String methodName = method.getName();
		// 标记目标方法是否要进行额外的操作
		boolean targetMethodIsExecuteExtraOption = false;
		boolean isIBeanWrapper = (object instanceof IBeanWrapper);
		// 假如是IBeanWrapper的实现类或接口，则需排查该接口中的方法
		if (isIBeanWrapper){
			targetMethodIsExecuteExtraOption = (!IgnoreUtils.methodInIBeanWrapper(methodName)
				// 判断是否是Object类中的方法，暂时只更具方法名称来判断
				|| !IgnoreUtils.methodInObject(methodName));
		}
		Object value = null;
		try {
			//执行目标方法前需要执行的额外逻辑
			if (targetMethodIsExecuteExtraOption){
				((IBeanWrapper) object).beforeExecute(method, params);
			}
			// 判断方法是否是抽象方法，是跳过执行目标方法
			if (!Modifier.isAbstract(method.getModifiers())){
				value = methodProxy.invokeSuper(object, params);
			}
			//执行目标方法后需要执行的额外逻辑
			if (targetMethodIsExecuteExtraOption){
				((IBeanWrapper) object).afterExecute(method, params);
			}
		} catch (Throwable e) {
			//执行抛出异常后需要处理的逻辑
			if (targetMethodIsExecuteExtraOption){
				((IBeanWrapper) object).throwExecute(method, params, e);
			}
			throw e;
		}
		return value;
	}

	/**
	 * 获取目标代理类型
	 * @return
	 */
	public Class<?> getTargetClass() {
		return targetClass;
	}
}
