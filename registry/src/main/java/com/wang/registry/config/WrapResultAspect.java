package com.wang.registry.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author wangju
 *
 */
@Component
@Aspect
public class WrapResultAspect {

	@Pointcut("@annotation(com.wang.registry.config.EnableResultWrap)")
	public void wrapPoint() {
	}

	@Around(value = "wrapPoint()")
	public Object wrap(ProceedingJoinPoint jp) {
		try {
			Object obj = jp.proceed();
			ReturnResultWrapper res = new ReturnResultWrapper();
			if (obj != null) {
				res.setData(obj);
			}
			return res.wrapper();
		} catch (Throwable e) {
			ErrorCodeWrapper err = new ErrorCodeWrapper(ErrorCode.ERR_SYSTEM_INTERNAL.getCode(),
					ErrorCode.ERR_SYSTEM_INTERNAL.getErrMsg(), e.toString());
			return err.wrapper();
		}
	}
}
