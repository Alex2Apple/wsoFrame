package com.wang.frame.start;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wang.frame.demo.AddService;
import com.wang.frame.demo.DivService;
import com.wang.frame.demo.MulService;
import com.wang.frame.demo.SubService;

@Component
public class ProxyTest {

	@Autowired
	private AddService addService;

	@Autowired
	private MulService mulService;

	@Autowired
	private SubService subService;

	@Autowired
	private DivService divService;

	public void test() {
		addService.op();
		mulService.op();
		subService.op();
		divService.op();
	}
}
