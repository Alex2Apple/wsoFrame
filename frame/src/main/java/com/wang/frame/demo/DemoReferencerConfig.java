package com.wang.frame.demo;

import com.wang.frame.bean.Referencer;

public class DemoReferencerConfig {

	@Referencer(id = "addService", service = "com.wang.frame.demo.AddService", version = "1.0")
	private String service1;

	@Referencer(id = "subService", service = "com.wang.frame.demo.SubService", version = "1.0")
	private String service2;

	@Referencer(id = "mulService", service = "com.wang.frame.demo.MulService", version = "1.0")
	private String service3;

	@Referencer(id = "divService", service = "com.wang.frame.demo.DivService", version = "1.0")
	private String service4;
}
