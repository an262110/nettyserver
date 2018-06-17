package com.netty.pac.server.applicationcontext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * 获取对象实例
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取bean实例
     *
     * @param serviceName
     * @return
     */
    public static Object getService(String serviceName) {
        return applicationContext.getBean(serviceName);
    }

    public static  Object getService(Class<?> var2){
        return applicationContext.getBean(var2);
    }
}
