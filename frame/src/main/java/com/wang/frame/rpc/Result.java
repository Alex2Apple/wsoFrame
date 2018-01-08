package com.wang.frame.rpc;

/**
 * @author wangju
 *
 */
public interface Result {

	Object getValue();

	Throwable getException();

	boolean isThrowExcepion();
}
