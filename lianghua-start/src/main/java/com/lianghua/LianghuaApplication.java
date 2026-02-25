package com.lianghua;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lianghua")
@MapperScan("com.lianghua.infrastructure.persistence.mapper")
public class LianghuaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LianghuaApplication.class, args);
    }
}
