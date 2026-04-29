package com.size.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 系统模块启动类
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.size")
public class SizeSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SizeSystemApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  系统模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
