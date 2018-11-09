package com.sun.proxy;


import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

class CglibProxy2 implements MethodInterceptor {

    //实现MethodInterceptor接口，定义方法的拦截器
    @Override
    public Object intercept(Object o, Method method, Object[] objects,
                            MethodProxy methodProxy) throws Throwable {
        System.out.println("pre1");
        //通过代理类调用父类中的方法,即实体类方法
        Object result = methodProxy.invokeSuper(o, objects);
        System.out.println("after1");
        return result;
    }
}

class CglibProxy implements MethodInterceptor {

    //实现MethodInterceptor接口，定义方法的拦截器
    @Override
    public Object intercept(Object o, Method method, Object[] objects,
                            MethodProxy methodProxy) throws Throwable {
        System.out.println("pre");
        //通过代理类调用父类中的方法,即实体类方法
        Object result = methodProxy.invokeSuper(o, objects);
        System.out.println("after");
        return result;
    }
}



public class CglibEnhancerTest {

    //定义委托类，可以不是接口
    static class serviceImpl {
        void say()
        {
            System.out.println("say");
        }
    }

    public static Object getProxyInstance(Object realSubject) {
        Enhancer enhancer = new Enhancer();
        //需要创建子类的类,即定义委托类
        enhancer.setSuperclass(realSubject.getClass());
        //设置两个CallBack以及CallbackFilter
        Callback[] callbacks=new Callback[1];
        callbacks[0]=new CglibProxy();
        //callbacks[1]=new CglibProxy2();
        enhancer.setCallbacks(callbacks);
        //通过字节码技术动态创建子类实例
        return enhancer.create();
    }

    public static void main(String[] args) {
        //将sam,class文件写到硬盘
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, ".//");
        //通过生成子类的方式创建代理类
        serviceImpl impl = (serviceImpl)getProxyInstance(new serviceImpl());
        impl.say();
    }
}
