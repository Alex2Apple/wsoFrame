package com.wang.wregistry.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * @author wangju
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.wang.wregistry.web" })
public class WebConfig extends WebMvcConfigurerAdapter {
	private static final String VIEWS_PATH = "/WEB-INF/views/";
	private static final String VIEWS_FILE_SUFFIX = ".jsp";

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix(VIEWS_PATH);
		resolver.setSuffix(VIEWS_FILE_SUFFIX);
		resolver.setExposeContextBeansAsAttributes(true);

		return resolver;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
