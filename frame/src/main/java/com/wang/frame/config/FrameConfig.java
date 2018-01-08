package com.wang.frame.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangju 配置示例 应用框架时在各自应用下指定配置类
 *
 */
@Configuration
@ComponentScan(basePackages = "com.wang.frame")
public class FrameConfig {

}
