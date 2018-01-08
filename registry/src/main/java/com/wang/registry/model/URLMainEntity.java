package com.wang.registry.model;

import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;

@Component
public class URLMainEntity {

	@Pattern(regexp = "[a-zA-Z]+(\\.[a-zA-Z]+)+", message = "接口名只能是字母和.组成, 如com.example.QueryService")
	private String service;

	private String version;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
