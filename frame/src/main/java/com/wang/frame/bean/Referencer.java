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
@Target(ElementType.FIELD)
@Documented
public @interface Referencer {

	/**
	 * @return
	 */
	String id();

	/**
	 * 引用接口名称
	 * 
	 * @return
	 */
	String service();

	/**
	 * 版本号
	 * 
	 * @return
	 */
	String version();

	/**
	 * 调用超时, 单位毫秒
	 * 
	 * @return
	 */
	int timeout() default 1000;

	/**
	 * 启动时是否连接检查
	 * 
	 * @return
	 */
	boolean force() default false;

	/**
	 * 是否是泛化调用, 如跨语言
	 * 
	 * @return
	 */
	boolean generic() default false;

	/**
	 * 是否懒加载
	 * 
	 * @return
	 */
	boolean lazy() default true;

	/**
	 * 过滤器
	 * 
	 * @return
	 */
	String[] filters() default {};
}
