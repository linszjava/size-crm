package com.size.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class SizeGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SizeGatewayApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  网关服务启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
