package com.netty.pac.server.dispatcher;

import com.netty.pac.rpc.entity.MethodInvokeMeta;
import com.netty.pac.rpc.entity.NullWritable;
import com.netty.pac.server.applicationcontext.ApplicationContextUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 请求分排器
 * <p>
 * create by 叶云轩 at 2018/3/3-下午1:31
 * contact by tdg_yyx@foxmail.com
 */
@Component
//public class RequestDispatcher implements ApplicationContextAware {
public class RequestDispatcher {
//    /**
//     * Spring上下文
//     */
//    private ApplicationContext app;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.app = applicationContext;
//    }

    /**
     * 发送
     *
     * @param ctx
     * @param invokeMeta
     */
    public void dispatcher(final ChannelHandlerContext ctx, final MethodInvokeMeta invokeMeta) {
        ChannelFuture f = null;
        try {
            Class<?> interfaceClass = invokeMeta.getInterfaceClass();
            String name = invokeMeta.getMethodName();
            Object[] args = invokeMeta.getArgs();
            Class<?>[] parameterTypes = invokeMeta.getParameterTypes();
//            Object targetObject = app.getBean(interfaceClass);
            Object targetObject = ApplicationContextUtils.getService(interfaceClass);
            Method method = targetObject.getClass().getMethod(name, parameterTypes);
            Object obj = method.invoke(targetObject, args);
            if (obj == null) {
                f = ctx.writeAndFlush(NullWritable.nullWritable());
            } else {
                f = ctx.writeAndFlush(obj);
            }
            f.addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            f = ctx.writeAndFlush(e.getMessage());
        } finally {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
