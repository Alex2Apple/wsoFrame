package com.wang.wregistry.model.item;

import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;

/**
 * @author wangju
 *
 */
@Component
public class MethodItem {

	@Pattern(regexp = "[a-zA-Z]+", message = "方法名只能是字母")
	private String methodName;

	private Class<?>[] parameterTypes;

	private Object[] attachments;

	private boolean isDeleted;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getAttachments() {
		return attachments;
	}

	public void setAttachments(Object[] attachments) {
		this.attachments = attachments;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
