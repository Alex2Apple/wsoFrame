package com.wang.frame.start;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.wang.frame.bean.ProviderBean;
import com.wang.frame.util.SpringContextUtil;

public class FrameStarter {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
				"com.wang.frame");
		applicationContext.start();
		ProviderBean<?> demo = SpringContextUtil.getBean("vp_DemoProviderService");
		System.out.println(demo.getId());
		System.out.println(demo.getService().getName());
		System.out.println(demo.getVersion());
		System.out.println(demo.getProtocol());

		ProxyTest proxyTest = SpringContextUtil.getBean("proxyTest");
		proxyTest.test();

		applicationContext.close();
	}
}
