package com.leyou.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger 接口文档配置类
 */
@Configuration
@EnableSwagger2
public class UserSwaggerConfig {
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                //指定接口文档地址
                .host("http://user.leyou.com")
                //指定接口文档信息
                .apiInfo(apiInfo())
                //查找 controller 层下的所有请求
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.leyou.user.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("乐优商城User服务")
                .description("乐优商城User服务接口文档")
                .version("1.0")
                .build();
    }
}
