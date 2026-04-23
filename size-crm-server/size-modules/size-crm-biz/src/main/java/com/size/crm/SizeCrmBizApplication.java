package com.size.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * CRM模块启动类
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.size")
public class SizeCrmBizApplication {
    public static void main(String[] args) {
        SpringApplication.run(SizeCrmBizApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  CRM-Biz核心模块启动成功 9202 biz  ლ(´ڡ`ლ)ﾞ  ");
    }
}
