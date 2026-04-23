package com.size.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 工作流模块启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
public class SizeWorkflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(SizeWorkflowApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  工作流模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
