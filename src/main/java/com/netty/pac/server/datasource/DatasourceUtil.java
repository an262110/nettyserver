package com.netty.pac.server.datasource;//package org.yyx.netty.server.datasource;
//
//
//import com.alibaba.druid.pool.DruidDataSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
//@EnableTransactionManagement(order = 2)
//public class DatasourceUtil {
//
//    private DruidProperties druidProperties;
//    /**
//     * 单数据源连接池配置
//     */
//    @Bean
//    @ConditionalOnProperty(prefix = "platform", name = "muti-datasource-open", havingValue = "false")
//    public DruidDataSource singleDatasource() {
//        return dataSourcePlatform();
//    }
//
//    /**
//     * platform的数据源
//     */
//    private DruidDataSource dataSourcePlatform() {
//        DruidDataSource dataSource = new DruidDataSource();
//        druidProperties.config(dataSource);
//        return dataSource;
//    }
//}
