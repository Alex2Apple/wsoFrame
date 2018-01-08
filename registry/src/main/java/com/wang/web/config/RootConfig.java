package com.wang.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author wangju
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.wang.registry.server", "com.wang.registry.center", "com.wang.registry.config",
		"com.wang.registry.model", "com.wang.registry.util", "com.wang.registry.cluster", "com.wang.center.factory",
		"com.wang.center.impl", "com.wang.data.source.impl" }, excludeFilters = {
				@Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class) })
@EnableAspectJAutoProxy
public class RootConfig {

}
