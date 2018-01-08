package com.wang.frame.demo;

import com.wang.frame.bean.Provider;

/**
 * @author wangju
 *
 */
@Provider(id = "demoProviderService", service = com.wang.frame.demo.DemoProviderService.class)
public interface DemoProviderService {

	void add();

	void sub();

	void mul();

	void div();
}
