package com.leyou.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer //声明这是一个eurekaserver
public class LyRegister {
    public static void main(String[] args) {
        SpringApplication.run(LyRegister.class, args);
    }
}
