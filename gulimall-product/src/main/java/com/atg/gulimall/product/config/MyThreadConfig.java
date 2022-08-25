package com.atg.gulimall.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
        //corePoolSize: 核心线程数
        //maximumPoolSize: 最大线程数
        //keepAliveTime: 空闲线程多长时间关闭
        //unit: 时间单位
        //workQueue: 阻塞队列长度
        //threadFactory 线程工厂
        //handler: 拒绝策略
       return new ThreadPoolExecutor(pool.getCoreSize(),
                pool.getMaxSize(),pool.getKeepAliveTime(),
                TimeUnit.SECONDS,new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
