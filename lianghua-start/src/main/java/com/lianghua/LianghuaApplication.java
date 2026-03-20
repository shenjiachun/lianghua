package com.lianghua;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 量化分析系统启动类
 * 基于COLA框架 + DDD架构
 * 集成阿里云通义千问 & 字节跳动豆包大模型
 */
@SpringBootApplication(scanBasePackages = "com.lianghua")
@MapperScan("com.lianghua.infrastructure.**.mapper")
public class LianghuaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LianghuaApplication.class, args);
    }
}
