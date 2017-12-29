package com.wang.wregistry.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author wangju
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.wang.wregistry.server", "com.wang.wregistry.config", "com.wang.wregistry.model",
		"com.wang.wregistry.util" }, excludeFilters = {
				@Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class) })
public class RootConfig {

}
