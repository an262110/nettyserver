package com.netty.pac.server.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.netty.pac.rpc.entity.User;
import com.netty.pac.rpc.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 测试Service实现类
 * <p>
 * create by 叶云轩 at 2018/3/3-下午1:50
 * contact by tdg_yyx@foxmail.com
 */
@Service
public class ImplDemoService implements DemoService {



    @Override
    public int sum(int numberA, int numberB) {
        return numberA + numberB;
    }

    @Override
    public String print() {
        return "这是来自服务器中DemoService接口的print方法打印的消息";
    }

    @Override
    public User getUserInfo() {
        User leader = new User();
        leader.setId(1);
        leader.setName("上级");
        leader.setSource(100);
        User user = new User();
        user.setSource(80);
        user.setId(0);
        user.setName("基层");
        user.setLeader(leader);
        return user;
    }
}
