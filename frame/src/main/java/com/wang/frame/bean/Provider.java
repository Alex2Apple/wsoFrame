package com.wang.frame.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wangju
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Provider {

	/**
	 * 服务id, 与接口的实现类匹配
	 * 
	 * @return
	 */
	String id();

	/**
	 * 接口类型
	 * 
	 * @return
	 */
	Class<?> service();

	/**
	 * 版本
	 * 
	 * @return
	 */
	String version() default "1.0";

	/**
	 * 协议
	 * 
	 * @return
	 */
	String protocol() default "wsof";

	/**
	 * 延迟暴露服务
	 * 
	 * @return
	 */
	boolean delay() default false;

	/**
	 * 延迟多久时间后暴露服务
	 * 
	 * @return
	 */
	int delayTime() default 3;
}
