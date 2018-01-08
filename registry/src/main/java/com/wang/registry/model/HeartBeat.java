package com.wang.registry.model;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * @author wangju
 *
 */
@Component
public class HeartBeat {

	private String lastTime;

	private String location;

	private boolean updated;

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime.toString();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
}
