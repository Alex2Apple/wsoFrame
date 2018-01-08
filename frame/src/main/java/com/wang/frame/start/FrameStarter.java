package com.wang.frame.start;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.wang.frame.bean.ProviderBean;

public class FrameStarter {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
				"com.wang.frame");
		applicationContext.start();

		applicationContext.close();
	}
}
