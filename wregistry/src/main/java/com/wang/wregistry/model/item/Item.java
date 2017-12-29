package com.wang.wregistry.model.item;

import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

@Component
public class Item {

	@Pattern(regexp = "[a-zA-Z]+(\\.[a-zA-Z]+)+", message = "接口名只能是字母和.组成, 如com.example.QueryService")
	private String interfaceName;

	@Size(min = 1, message = "接口的方法至少要求一个")
	private List<MethodItem> methodItems;

	@Pattern(regexp = "\\d+(\\.\\w)", message = "版本号只能是数字 . 字母组成, 起始必须是数字, 如1.0")
	private String version;

	private String host;

	private int port;

	private Object protocol;

	@Pattern(regexp = "[a-zA-Z]+", message = "应用名只能是字母")
	private String appName;

	private boolean deleted;

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public List<MethodItem> getMethodItems() {
		return methodItems;
	}

	public void setMethodItems(List<MethodItem> methodItems) {
		this.methodItems = methodItems;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Object getProtocol() {
		return protocol;
	}

	public void setProtocol(Object protocol) {
		this.protocol = protocol;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
