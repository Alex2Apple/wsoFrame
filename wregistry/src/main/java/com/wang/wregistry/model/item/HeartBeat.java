package com.wang.wregistry.model.item;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * @author wangju
 *
 */
@Component
public class HeartBeat {

	private String lastTime;

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime.toString();
	}
}
